package tv.accedo.dishonstream2.data.model.template.swimlane

import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class SwimLaneWidgetContainerData(
    val meta: MetaData,
    val widgetContents: List<String>,
    val title: String
)