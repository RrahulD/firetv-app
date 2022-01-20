package tv.accedo.dishonstream2.domain.model.vod

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VODService(
    val name: String,
    val deeplinkUrl: String
) : Parcelable

data class MovieDetails(
    val id: String,
    val title: String,
    val overview: String,
    val classification: String?,
    val runTime: Int,
    val posterUrl: String,
    val backDropUrl: String,
    val genres: List<String>,
    val vodServices: List<VODService>
)

@Parcelize
data class ShowDetails(
    val id: String,
    val title: String,
    val overview: String,
    val classification: String?,
    val posterUrl: String,
    val backDropUrl: String,
    val genres: List<String>,
    val seasons: List<Int>
) : Parcelable

data class EpisodeDetail(
    val id: String,
    val title: String,
    val episodeNo: Double,
    val screenshotUrl: String,
    val overview: String,
    val vodServices: List<VODService>
)