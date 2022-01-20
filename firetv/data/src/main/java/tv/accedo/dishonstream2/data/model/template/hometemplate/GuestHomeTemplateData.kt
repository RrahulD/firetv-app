package tv.accedo.dishonstream2.data.model.template.hometemplate

import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class GuestHomeTemplateData(
    val meta: MetaData,
    val widgetColumn1: String,
    val widgetColumn2: String,
    val widgetColumn3: String,
    val title: String
)