package tv.accedo.dishonstream2.data.model.reelgoodvod.base


import com.google.gson.annotations.SerializedName
import tv.accedo.dishonstream2.domain.model.vod.Movie
import tv.accedo.dishonstream2.domain.model.vod.Show

data class VODContentResult(
    val result: List<VODContentData>
)

data class VODContentData(
    @SerializedName("content_type")
    val contentType: String,
    val id: String,
    @SerializedName("poster_url")
    val posterUrl: String,
    @SerializedName("released_on")
    val releasedOn: String,
    val slug: String,
    val title: String
) {
    fun toMovie() = Movie(id, title, posterUrl)

    fun toShow() = Show(id, title, posterUrl)
}