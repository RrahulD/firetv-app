package tv.accedo.dishonstream2.data.model.template.widget

import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class LiveChannelWidgetData(
    val meta: MetaData,
    val title: String,
    val channelId: Long
)
