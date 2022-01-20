package tv.accedo.dishonstream2.data.model.reelgoodvod.movie


import com.google.gson.annotations.SerializedName
import tv.accedo.dishonstream2.data.model.reelgoodvod.base.Availability

data class MovieDetailsResult(
    val result: MovieDetailsData
)

data class MovieDetailsData(
    val availability: List<Availability>,
    @SerializedName("backdrop_url")
    val backdropUrl: String,
    val classification: String?,
    val genres: List<Int>,
    val id: String,
    val overview: String,
    @SerializedName("poster_url")
    val posterUrl: String,
    @SerializedName("released_on")
    val releasedOn: String,
    val runtime: Int,
    val title: String
)