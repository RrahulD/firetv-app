package tv.accedo.dishonstream2.data.model.weather

import com.google.gson.annotations.SerializedName
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import tv.accedo.dishonstream2.domain.model.home.weather.WeatherForeCast

data class WeatherData(
    val error: String?,
    val response: WeatherResponse?,
    val success: Boolean
)

data class WeatherResponse(
    @SerializedName("forecastday")
    val foreCastDays: List<ForeCast>,
    val observation: Observation
)

data class Observation(
    val id: String,
    val place: Place,
)

data class ForeCast(
    val timestamp: Long,
    val avgTempC: Double,
    val avgTempF: Double,
    val icon: String,
    val maxTempC: Double,
    val maxTempF: Double,
    val minTempC: Double,
    val minTempF: Double,
    val weather: String,
    val weatherPrimary: String,
) {
    fun toWeatherForeCast(imageBase: String): WeatherForeCast =
        WeatherForeCast(
            day = Instant.fromEpochMilliseconds(timestamp * 1000)
                .toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.toString().substring(0..2),
            avgTempF = avgTempF,
            avgTempC = avgTempC,
            maxTempF = maxTempF,
            maxTempC = maxTempC,
            minTempF = minTempF,
            minTempC = minTempC,
            description = weatherPrimary,
            iconUrl = "$imageBase/$icon".replace("http", "https")
        )
}

data class Place(
    val city: String,
    val country: String,
    val name: String,
    val state: String
)

