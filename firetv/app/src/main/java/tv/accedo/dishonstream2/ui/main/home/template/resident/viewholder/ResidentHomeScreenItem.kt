package tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder

import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneContainer
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget

sealed class ResidentHomeScreenItem {
    class HeroCarousel(val carouselWidgets: List<Widget>) : ResidentHomeScreenItem()
    class SwimLane(val swimLaneContainer: SwimLaneContainer) : ResidentHomeScreenItem()
}
