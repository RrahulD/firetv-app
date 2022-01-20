package tv.accedo.dishonstream2.domain.model.home.template.widget

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tv.accedo.dishonstream2.domain.model.home.template.base.Image
import tv.accedo.dishonstream2.domain.model.home.template.other.MoreInfoContent

@Parcelize
data class MoreInfoWidget(
    val title: String,
    val subTitle: String,
    val headerImage: Image,
    val contents: List<MoreInfoContent>,
    val name: String,
    val icon: Image,
    val link: String,
    val contentTitle: String
) : Widget, Parcelable
