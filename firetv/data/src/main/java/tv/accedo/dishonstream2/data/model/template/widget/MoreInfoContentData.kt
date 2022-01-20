package tv.accedo.dishonstream2.data.model.template.widget

import tv.accedo.dishonstream2.data.model.base.ImageData
import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class MoreInfoContentData(
    val meta: MetaData,
    val header: String,
    val paragraph: String,
    val image: ImageData?,
)