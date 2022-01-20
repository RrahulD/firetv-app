package tv.accedo.dishonstream2.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.data.model.reelgoodvod.base.Availability
import tv.accedo.dishonstream2.data.model.reelgoodvod.base.VODServiceData
import tv.accedo.dishonstream2.data.networking.client.DishRetrofitClient
import tv.accedo.dishonstream2.data.networking.service.ContentType
import tv.accedo.dishonstream2.data.networking.service.ReelGoodApiService
import tv.accedo.dishonstream2.data.networking.service.Sort
import tv.accedo.dishonstream2.domain.model.vod.*
import tv.accedo.dishonstream2.domain.repository.VODRepository

class ReelGoodVODRepository(
    private val dishRetrofitClient: DishRetrofitClient,
) : VODRepository {

    companion object {
        private const val TYPE_MOVIE = "Movie"
        private const val TYPE_SHOW = "Show"
    }

    private val reelGoodApiService: ReelGoodApiService by lazy {
        dishRetrofitClient.getReelGodApiService("https://dishonstream-api.herokuapp.com")
    }
    private var genresMap: Map<Int, String>? = null
    private var vodServicesMap: Map<Int, VODServiceData>? = null

    override suspend fun getPopularContent(count: Int, page: Int): List<VODContent> = with(Dispatchers.IO) {
        val popularContentResult =
            reelGoodApiService.getPopularContent(ContentType.BOTH.ordinal, Sort.POPULAR.ordinal, count, page)
        popularContentResult.result.map {
            if (it.contentType.equals(TYPE_MOVIE, true))
                it.toMovie() else it.toShow()
        }
    }

    override suspend fun getTrendingContents(count: Int, page: Int): List<VODContent> = with(Dispatchers.IO) {
        val popularContentResult =
            reelGoodApiService.getPopularContent(ContentType.BOTH.ordinal, Sort.TRENDING.ordinal, count, page)
        popularContentResult.result.map {
            if (it.contentType.equals(TYPE_MOVIE, true))
                it.toMovie() else it.toShow()
        }
    }

    override suspend fun getPopularMovies(count: Int, page: Int): List<Movie> = withContext(Dispatchers.IO) {
        val popularMoviesResult =
            reelGoodApiService.getPopularContent(ContentType.MOVIES.ordinal, Sort.POPULAR.ordinal, count, page)
        popularMoviesResult.result.filter { it.contentType.equals(TYPE_MOVIE) }.map { it.toMovie() }
    }

    override suspend fun getPopularShows(count: Int, page: Int): List<Show> = withContext(Dispatchers.IO) {
        val popularShowsResult =
            reelGoodApiService.getPopularContent(ContentType.SHOWS.ordinal, Sort.POPULAR.ordinal, count, page)
        popularShowsResult.result.filter { it.contentType.equals(TYPE_SHOW) }.map { it.toShow() }
    }

    override suspend fun getMovieDetails(movieId: String): MovieDetails = withContext(Dispatchers.IO) {
        val movieDetailsData = reelGoodApiService.getMovieDetails(movieId).result
        MovieDetails(
            id = movieDetailsData.id,
            title = movieDetailsData.title,
            overview = movieDetailsData.overview,
            classification = movieDetailsData.classification,
            runTime = movieDetailsData.runtime,
            posterUrl = movieDetailsData.posterUrl,
            backDropUrl = movieDetailsData.backdropUrl,
            genres = movieDetailsData.genres.map { getGenresMap()[it] ?: "" },
            vodServices = mapAvailabilityToVODService(movieDetailsData.availability)
        )
    }

    override suspend fun getShowDetails(showId: String): ShowDetails = withContext(Dispatchers.IO) {
        val showDetails = reelGoodApiService.getShowDetails(showId).result
        ShowDetails(
            id = showDetails.id,
            title = showDetails.title,
            classification = showDetails.classification,
            overview = showDetails.overview,
            posterUrl = showDetails.posterUrl,
            backDropUrl = showDetails.backdropUrl,
            genres = showDetails.genres.map { getGenresMap()[it] ?: "" },
            seasons = showDetails.seasonNumbers
        )
    }

    override suspend fun getSeasonDetails(showId: String, seasonNo: Int): List<EpisodeDetail> = withContext(Dispatchers.IO) {
        val seasonDetails = reelGoodApiService.getSeasonDetails(showId, seasonNo)

        seasonDetails.result.map {
            EpisodeDetail(
                id = it.id,
                title = it.title,
                screenshotUrl = it.screenshotUrl,
                episodeNo = it.number,
                overview = it.overview,
                vodServices = mapAvailabilityToVODService(it.availability)
            )
        }
    }

    override suspend fun getEpisodeDetails(showId: String, seasonNo: Int, episodeNo: Int): EpisodeDetail? {
        val seasonDetails = reelGoodApiService.getSeasonDetails(showId, seasonNo)
        if (episodeNo - 1 >= 0 && episodeNo - 1 < seasonDetails.result.size) {
            val episodeData = seasonDetails.result[episodeNo - 1]
            return EpisodeDetail(
                id = episodeData.id,
                title = episodeData.title,
                screenshotUrl = episodeData.screenshotUrl,
                episodeNo = episodeData.number,
                overview = episodeData.overview,
                vodServices = mapAvailabilityToVODService(episodeData.availability)
            )
        }
        return null
    }

    override suspend fun searchContent(searchText: String): List<VODContent> = withContext(Dispatchers.IO) {
        val searchResults = reelGoodApiService.searchVodContent(searchText)
        val result: MutableList<VODContent> = ArrayList()
        searchResults.result.vod.forEach { vodSearch ->
            vodSearch.currentChunk.map {
                result.add(if (it.contentType == TYPE_MOVIE) it.toMovie() else it.toShow())
            }
        }
        result
    }

    private suspend fun mapAvailabilityToVODService(availability: List<Availability>): List<VODService> =
        withContext(Dispatchers.IO) {
            availability.map {
                val serviceName = getVODServicesMap()[it.serviceGroupId]?.displayName
                val deeplinkUrl: String = it.links.android ?: ""
                if (serviceName.isNullOrBlank() || deeplinkUrl.isBlank()) null
                else VODService(serviceName, deeplinkUrl)
            }.filterNotNull()
        }

    private suspend fun getGenresMap(): Map<Int, String> = withContext(Dispatchers.IO) {
        genresMap ?: mapOf(*reelGoodApiService.getGenres().result.map { it.id to it.name }.toTypedArray()).also {
            genresMap = it
        }
    }

    private suspend fun getVODServicesMap(): Map<Int, VODServiceData> = withContext(Dispatchers.IO) {
        vodServicesMap ?: mapOf(*reelGoodApiService.getVODServices().result.map { it.id to it }.toTypedArray()).also {
            vodServicesMap = it
        }
    }
}