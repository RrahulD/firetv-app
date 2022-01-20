package tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory

import android.view.ViewGroup
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder

enum class WidgetViewHolderType {
    CAROUSEL, STANDARD, LARGE
}

class WidgetViewHolderFactory(
    private val standardWidgetViewHolderFactory: StandardWidgetViewHolderFactory,
    private val largeWidgetViewHolderFactory: LargeWidgetViewHolderFactory,
    private val carouselWidgetViewHolderFactory: CarouselWidgetViewHolderFactory
) {

    fun getWidgetViewHolder(
        widget: Widget,
        baseFragment: BaseFragment,
        parent: ViewGroup,
        type: WidgetViewHolderType,
        isZoomEnabled: Boolean = false
    ): WidgetViewHolder {
        return when (type) {
            WidgetViewHolderType.CAROUSEL ->
                carouselWidgetViewHolderFactory.getWidgetViewHolder(widget, baseFragment, parent, isZoomEnabled)
            WidgetViewHolderType.STANDARD ->
                standardWidgetViewHolderFactory.getWidgetViewHolder(widget, baseFragment, parent, isZoomEnabled)
            WidgetViewHolderType.LARGE ->
                largeWidgetViewHolderFactory.getWidgetViewHolder(widget, baseFragment, parent, isZoomEnabled)
        }
    }
}