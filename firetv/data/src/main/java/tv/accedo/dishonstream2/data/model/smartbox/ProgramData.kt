package tv.accedo.dishonstream2.data.model.smartbox

import com.google.gson.annotations.SerializedName

data class ProgramData(
    val eventName: String,
    val startTime: String,
    val duration: Int,
    @SerializedName("echostarId") val echoStarId: String
)