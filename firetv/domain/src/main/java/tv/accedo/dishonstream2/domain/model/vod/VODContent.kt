package tv.accedo.dishonstream2.domain.model.vod

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


sealed class VODContent : Parcelable

@Parcelize
data class Movie(
    val id: String,
    val title: String,
    val posterImage: String
) : VODContent()

@Parcelize
data class Show(
    val id: String,
    val title: String,
    val posterImage: String
) : VODContent()
