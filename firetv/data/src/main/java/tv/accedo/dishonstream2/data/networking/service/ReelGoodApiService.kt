package tv.accedo.dishonstream2.data.networking.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tv.accedo.dishonstream2.data.model.reelgoodvod.base.GenreResult
import tv.accedo.dishonstream2.data.model.reelgoodvod.base.VODContentResult
import tv.accedo.dishonstream2.data.model.reelgoodvod.base.VODServiceResults
import tv.accedo.dishonstream2.data.model.reelgoodvod.movie.MovieDetailsResult
import tv.accedo.dishonstream2.data.model.reelgoodvod.search.VODSearchResult
import tv.accedo.dishonstream2.data.model.reelgoodvod.show.SeasonDetailsResult
import tv.accedo.dishonstream2.data.model.reelgoodvod.show.ShowDetailsResult

enum class ContentType {
    BOTH,
    SHOWS,
    MOVIES
}

enum class Sort {
    POPULAR,
    TRENDING,
}

interface ReelGoodApiService {
    @GET("api/vod/popular")
    suspend fun getPopularContent(
        @Query("contentType") contentType: Int,
        @Query("sort") sort: Int,
        @Query("count") count: Int,
        @Query("page") page: Int
    ): VODContentResult

    @GET("api/vod/movie/{id}")
    suspend fun getMovieDetails(@Path("id") id: String): MovieDetailsResult

    @GET("api/vod/show/{id}")
    suspend fun getShowDetails(@Path("id") id: String): ShowDetailsResult

    @GET("api/vod/show/{id}/season/{number}")
    suspend fun getSeasonDetails(@Path("id") showId: String, @Path("number") seasonNumber: Int): SeasonDetailsResult

    @GET("api/vod/genres")
    suspend fun getGenres(): GenreResult

    @GET("api/vod/services")
    suspend fun getVODServices(): VODServiceResults

    @GET("api/search/get-on-demand-search-results/{searchText}")
    suspend fun searchVodContent(@Path("searchText") searchText: String): VODSearchResult
}