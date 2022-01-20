package tv.accedo.dishonstream2.data.networking.service

import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tv.accedo.dishonstream2.data.model.game.CurrentGamesData

interface NagrastarApiService {

    @GET("api/games")
    suspend fun getCurrentGames(@Query("startDate") startDate: String, @Query("endDate") endDate: String): CurrentGamesData

    @GET("api/stats/{gameId}")
    suspend fun getGameStats(@Path("gameId") gameId: Long): JsonObject
}