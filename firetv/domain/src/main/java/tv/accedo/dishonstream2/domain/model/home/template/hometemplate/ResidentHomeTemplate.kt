package tv.accedo.dishonstream2.domain.model.home.template.hometemplate

import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneContainer
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget

data class ResidentHomeTemplate(
    val contentContainers: List<SwimLaneContainer>,
    val heroCarouselItems: List<Widget>,
    val title: String
) : HomeTemplate

