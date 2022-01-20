package tv.accedo.dishonstream2.data.model.reelgoodvod.show


import com.google.gson.annotations.SerializedName
import tv.accedo.dishonstream2.data.model.reelgoodvod.base.Availability

data class SeasonDetailsResult(
    val result: List<EpisodeData>
)

data class EpisodeData(
    val availability: List<Availability>,
    val id: String,
    val number: Double,
    val overview: String,
    @SerializedName("released_on")
    val releasedOn: String,
    @SerializedName("screenshot_url")
    val screenshotUrl: String,
    val title: String
)