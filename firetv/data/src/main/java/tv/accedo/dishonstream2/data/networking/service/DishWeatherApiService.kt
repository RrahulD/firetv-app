package tv.accedo.dishonstream2.data.networking.service

import retrofit2.http.GET
import retrofit2.http.Query
import tv.accedo.dishonstream2.data.model.weather.WeatherData

interface DishWeatherApiService {

    @GET("weatherdata")
    suspend fun getWeatherForeCast(@Query("p") zipCode: String): WeatherData
}