package tv.accedo.dishonstream2.domain.model.home.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    val place: String,
    val todayForeCast: WeatherForeCast,
    val futureForecast: List<WeatherForeCast>
) : Parcelable