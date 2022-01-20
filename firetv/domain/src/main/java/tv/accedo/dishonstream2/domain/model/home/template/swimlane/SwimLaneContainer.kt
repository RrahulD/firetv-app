package tv.accedo.dishonstream2.domain.model.home.template.swimlane

import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget

data class SwimLaneContainer(
    val widgets: List<Widget>,
    val title: String,
    val type: SwimLaneType
)

enum class SwimLaneType {
    NORMAL,
    LARGE
}
