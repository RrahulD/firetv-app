package tv.accedo.dishonstream2.domain.model.supair

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Channel(
    val name: String,
    val contentId: String,
    val serviceKey: Long,
    val URLHLS: String,
    val URLDASH: String,
    var programs: List<Program>,
    val detail: ChannelInfo?
) : Parcelable