package tv.accedo.dishonstream2.data.model.template.swimlane

import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class SwimLaneWidgetLargeContainerData(
    val meta: MetaData,
    val widgetContents: List<String>,
    val title: String
)