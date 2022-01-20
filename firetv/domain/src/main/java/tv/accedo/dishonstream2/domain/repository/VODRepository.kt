package tv.accedo.dishonstream2.domain.repository

import tv.accedo.dishonstream2.domain.model.vod.*

interface VODRepository {

    suspend fun getPopularContent(count: Int, page: Int): List<VODContent>

    suspend fun getTrendingContents(count: Int, page: Int): List<VODContent>

    suspend fun getPopularMovies(count: Int, page: Int): List<Movie>

    suspend fun getPopularShows(count: Int, page: Int): List<Show>

    suspend fun getMovieDetails(movieId: String): MovieDetails

    suspend fun getShowDetails(showId: String): ShowDetails

    suspend fun getSeasonDetails(showId: String, seasonNo: Int): List<EpisodeDetail>

    suspend fun getEpisodeDetails(showId: String, seasonNo: Int, episodeNo: Int): EpisodeDetail?

    suspend fun searchContent(searchText: String): List<VODContent>

}