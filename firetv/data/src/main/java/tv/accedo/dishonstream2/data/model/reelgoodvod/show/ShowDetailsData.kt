package tv.accedo.dishonstream2.data.model.reelgoodvod.show


import com.google.gson.annotations.SerializedName

data class ShowDetailsResult(
    val result: ShowDetailsData
)

data class ShowDetailsData(
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
    @SerializedName("season_numbers")
    val seasonNumbers: List<Int>,
    val title: String
)