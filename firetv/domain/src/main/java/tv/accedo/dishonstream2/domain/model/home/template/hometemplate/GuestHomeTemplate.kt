package tv.accedo.dishonstream2.domain.model.home.template.hometemplate

import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneContainer

data class GuestHomeTemplate(
    val leftColumn: SwimLaneContainer,
    val centerColumn: SwimLaneContainer,
    val rightColumn: SwimLaneContainer
) : HomeTemplate
