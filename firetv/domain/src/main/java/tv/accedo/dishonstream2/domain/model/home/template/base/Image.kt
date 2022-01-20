package tv.accedo.dishonstream2.domain.model.home.template.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val fileUrl: String,
):Parcelable