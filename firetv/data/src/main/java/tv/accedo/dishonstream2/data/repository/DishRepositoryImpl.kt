package tv.accedo.dishonstream2.data.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import timber.log.Timber
import tv.accedo.dishonstream2.data.datahelper.EpgDataHelper
import tv.accedo.dishonstream2.data.datahelper.SportsDataHelper
import tv.accedo.dishonstream2.data.db.dao.ChannelDao
import tv.accedo.dishonstream2.data.db.model.ChannelEntity
import tv.accedo.dishonstream2.data.model.smartbox.ServiceData
import tv.accedo.dishonstream2.data.networking.client.DishRetrofitClient
import tv.accedo.dishonstream2.data.networking.service.DishSmartboxApiService
import tv.accedo.dishonstream2.data.networking.service.DishSupairApiService
import tv.accedo.dishonstream2.data.networking.service.DishWeatherApiService
import tv.accedo.dishonstream2.data.networking.service.NagrastarApiService
import tv.accedo.dishonstream2.data.repository.caching.CacheStore
import tv.accedo.dishonstream2.domain.model.control.Endpoints
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.League
import tv.accedo.dishonstream2.domain.model.home.game.stat.GameStats
import tv.accedo.dishonstream2.domain.model.home.weather.Weather
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.ChannelInfo
import tv.accedo.dishonstream2.domain.model.supair.DRM
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.domain.repository.DishRepository
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DishRepositoryImpl(
    private val dishRetrofitClient: DishRetrofitClient,
    private val localChannelDataSource: ChannelDao,
    private val epgDataHelper: EpgDataHelper,
    private val cacheStore: CacheStore,
    private val sportsDataHelper: SportsDataHelper
) : DishRepository {
    private lateinit var endpoints: Endpoints
    private val dishSmartBoxApiService: DishSmartboxApiService by lazy { dishRetrofitClient.getDishSmartboxApiService(endpoints.atx) }
    private val dishSupairApiService: DishSupairApiService by lazy { dishRetrofitClient.getDishSupairApiService(endpoints.supair) }
    private val dishWeatherApiService: DishWeatherApiService by lazy { dishRetrofitClient.getDishWeatherApiService(endpoints.weatherBase) }
    private val nagrastarApiService: NagrastarApiService by lazy { dishRetrofitClient.getNagrastarApiService(endpoints.sports) }

    private val mutex = Mutex()

    companion object {
        private const val CACHE_KEY_EPG = "epg_cache"
        private const val CACHE_KEY_PROGRAM_INFO = "program_info_cache"
    }

    override suspend fun initializeEndpoints(endpoints: Endpoints) {
        this.endpoints = endpoints
        Timber.d("Endpoints => $endpoints")
    }

    override suspend fun getEpg(): List<Channel> = supervisorScope {
        mutex.withLock {
            val cachedEpg = cacheStore.get<List<Channel>>(CACHE_KEY_EPG)
            if (cachedEpg != null) {
                Timber.d("Cache: Returning EPG from cache")
                cachedEpg.forEach { it.programs = epgDataHelper.cleanPrograms(it.programs) }
                return@supervisorScope cachedEpg
            }

            Timber.d("Cache:Making api call for EPG")
            val epdData = dishSmartBoxApiService.getEPG(endpoints.epg)
            val asyncHandlesMap = hashMapOf<ServiceData, Deferred<ChannelEntity>>()
            for (service in epdData.services) {
                asyncHandlesMap[service] = async {
                    localChannelDataSource.getChannelByServiceName(service.serviceName)
                        ?: epgDataHelper.mapServiceDataAndChannelInfoDataToChannelEntity(
                            service,
                            dishSupairApiService.getChannelInfo(service.serviceKey)
                        ).also { channel ->
                            localChannelDataSource.insertChannel(channel)
                        }
                }
            }

            val serviceDataToChannelMap = hashMapOf<ServiceData, ChannelEntity?>()
            for ((serviceData, asyncHandle) in asyncHandlesMap) {
                try {
                    serviceDataToChannelMap[serviceData] = asyncHandle.await()
                } catch (ex: Exception) {
                    Timber.e("Error while fetching channel info for ${serviceData.serviceName}")
                    serviceDataToChannelMap[serviceData] = null
                }
            }

            val serviceDataToChannelEntityMap = epgDataHelper.fromChannelEntityMapToChannelMap(serviceDataToChannelMap)

            // for maintaining the epg order as returned by smartbox
            epdData.services.mapNotNull { serviceData ->
                serviceDataToChannelEntityMap[serviceData]
            }.also {
                Timber.d("Cache: Storing EPG to cache")
                if (it.isNotEmpty()) cacheStore.store(CACHE_KEY_EPG, it, Duration.minutes(15))
            }
        }
    }

    override suspend fun getProgramInfo(echoStarId: String): ProgramInfo = withContext(Dispatchers.IO) {
        val cachedProgramInfo = cacheStore.get<ProgramInfo>("$CACHE_KEY_PROGRAM_INFO-$echoStarId")
        if (cachedProgramInfo != null) {
            Timber.d("Cache: Returning cached program info for $CACHE_KEY_PROGRAM_INFO-$echoStarId")
            return@withContext cachedProgramInfo
        }

        Timber.d("Cache: Making Api call for program info for $CACHE_KEY_PROGRAM_INFO-$echoStarId")
        dishSupairApiService.getProgramInfo(echoStarId).toProgramInfoEntity().also {
            Timber.d("Cache: Storing program info for $CACHE_KEY_PROGRAM_INFO-$echoStarId")
            cacheStore.store("$CACHE_KEY_PROGRAM_INFO-$echoStarId", it, Duration.INFINITE)
        }
    }

    override suspend fun getChannelInfo(serviceName: String): ChannelInfo? = withContext(Dispatchers.IO) {
        localChannelDataSource.getChannelByServiceName(serviceName)?.toChannelInfoEntity()
    }

    override suspend fun getChannelById(channelId: Long): Channel? = withContext(Dispatchers.IO) {
        getEpg().find { it.serviceKey == channelId }
    }

    override suspend fun getDRMToken(contentID: String): DRM = withContext(Dispatchers.IO) {
        dishSmartBoxApiService.getDRMToken(contentID).toDRMEntity()
    }

    override suspend fun getWeatherDetail(zipCode: String): Weather = withContext(Dispatchers.IO) {
        val weatherData = dishWeatherApiService.getWeatherForeCast(zipCode)
        val weatherResponse = weatherData.response
        if (!weatherData.success || weatherResponse == null
            || weatherResponse.foreCastDays.isEmpty()
        ) throw RuntimeException(
            weatherData.error ?: "Something went wrong"
        )

        val place = weatherResponse.observation.place.city.split("/").first()
        val todayForeCast = weatherResponse.foreCastDays.first().toWeatherForeCast(endpoints.weatherImage)
        val futureForeCast = weatherResponse.foreCastDays.subList(
            1, minOf(6, weatherResponse.foreCastDays.size)
        ).map { it.toWeatherForeCast(endpoints.weatherImage) }

        Weather(
            place,
            todayForeCast,
            futureForeCast
        )
    }

    override suspend fun getCurrentGames(startDate: String, endDate: String): List<Game> = withContext(Dispatchers.IO) {
        sportsDataHelper.mapCurrentGamesDataToGameList(nagrastarApiService.getCurrentGames(startDate, endDate))
    }

    override suspend fun getGameStats(gameId: Long, league: League): GameStats = withContext(Dispatchers.IO) {
        val gameStatsJSON = nagrastarApiService.getGameStats(gameId)
        sportsDataHelper.parseGameStatsFromJson(gameStatsJSON, league)
    }
}
