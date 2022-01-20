package tv.accedo.dishonstream2.data.model.template.widget

import tv.accedo.dishonstream2.data.model.base.ImageData
import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class MoreInfoWidgetData(
    val meta: MetaData,
    val title:String,
    val subTitle: String,
    val headerImage: ImageData,
    val contents: List<String>,
    val name: String,
    val icon: ImageData,
    val link: String,
    val contentTitle: String
)
