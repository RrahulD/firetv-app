package tv.accedo.dishonstream2.domain.repository

import tv.accedo.dishonstream2.domain.model.control.Endpoints
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.League
import tv.accedo.dishonstream2.domain.model.home.game.stat.GameStats
import tv.accedo.dishonstream2.domain.model.home.weather.Weather
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.ChannelInfo
import tv.accedo.dishonstream2.domain.model.supair.DRM
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo

interface DishRepository {
    suspend fun initializeEndpoints(endpoints: Endpoints)

    suspend fun getEpg(): List<Channel>

    suspend fun getProgramInfo(echoStarId: String): ProgramInfo

    suspend fun getChannelInfo(serviceName: String): ChannelInfo?

    suspend fun getChannelById(channelId: Long): Channel?

    suspend fun getDRMToken(contentID: String): DRM

    suspend fun getWeatherDetail(zipCode: String): Weather

    suspend fun getCurrentGames(startDate: String, endDate: String): List<Game>

    suspend fun getGameStats(gameId: Long, league: League): GameStats
}