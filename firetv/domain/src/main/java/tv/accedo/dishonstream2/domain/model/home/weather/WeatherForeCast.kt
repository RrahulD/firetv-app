package tv.accedo.dishonstream2.domain.model.home.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherForeCast(
    val day: String,
    val avgTempF: Double,
    val maxTempF: Double,
    val minTempF: Double,
    val avgTempC: Double,
    val maxTempC: Double,
    val minTempC: Double,
    val description: String,
    val iconUrl: String
):Parcelable

