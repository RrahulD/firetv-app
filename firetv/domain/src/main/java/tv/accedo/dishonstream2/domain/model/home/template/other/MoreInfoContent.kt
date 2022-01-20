package tv.accedo.dishonstream2.domain.model.home.template.other

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tv.accedo.dishonstream2.domain.model.home.template.base.Image

@Parcelize
data class MoreInfoContent(
    val header: String,
    val paragraph: String,
    val image: Image
) : Parcelable
