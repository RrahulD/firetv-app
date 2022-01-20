package tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.contentcontainer

import android.util.TypedValue
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.HomeScreenResidentSwimlaneLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneContainer
import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneType
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget
import tv.accedo.dishonstream2.extensions.capitalise
import tv.accedo.dishonstream2.extensions.consumeLeftKeyEvent
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.swimlane.SwimLane
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderType
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.util.WidgetUIHelper
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class ResidentHomeScreenSwimLaneViewHolder(
    private val binding: HomeScreenResidentSwimlaneLayoutBinding,
    private val baseFragment: BaseFragment,
    private val widgetViewHolderFactory: WidgetViewHolderFactory,
    private val widgetUIHelper: WidgetUIHelper,
    private val isZoomEnabled: Boolean
) : RecyclerView.ViewHolder(binding.root), KoinComponent {

    private val themeManager: ThemeManager by inject()

    fun bind(swimLaneContainer: SwimLaneContainer) {
        with(binding) {
            val resource = swimLane.context.resources
            if (swimLaneContainer.type == SwimLaneType.LARGE) {
                swimLane.setItemHeight(resource.getDimensionPixelSize(R.dimen._200dp))
                swimLane.setItemWidth(resource.getDimensionPixelSize(R.dimen._250dp))
            } else {
                swimLane.setItemHeight(resource.getDimensionPixelSize(R.dimen._100dp))
                swimLane.setItemWidth(resource.getDimensionPixelSize(R.dimen._200dp))
            }
            if (isZoomEnabled) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            }
            title.text = swimLaneContainer.title.capitalise()
            swimLane.setDataSource(object : SwimLane.DataSource() {
                val swimLaneType = swimLaneContainer.type

                override fun type(): SwimLaneType = swimLaneType

                override fun getWidgets(): List<Widget> = swimLaneContainer.widgets

                override fun onCreateWidgetViewHolder(parent: ViewGroup, widget: Widget): WidgetViewHolder {
                    return widgetViewHolderFactory.getWidgetViewHolder(
                        widget, baseFragment, parent, swimLaneType.toWidgetViewHolderType(), isZoomEnabled
                    )
                }

                override fun onBindWidgetViewHolder(
                    widgetViewHolder: WidgetViewHolder,
                    widget: Widget,
                    position: Int,
                    itemsCount: Int,
                    isLargeSwimLane: Boolean
                ) {
                    widgetUIHelper.populateWidget(widget, widgetViewHolder, isLargeSwimLane)
                    if (position == 0) widgetViewHolder.focusView.consumeLeftKeyEvent()
                    if (position == itemsCount - 1) widgetViewHolder.focusView.consumeRightKeyEvent()
                    themeManager.getWidgetBackgroundDrawable()?.let { widgetViewHolder.focusView.background = it }
                }
            })
        }
    }

    private fun SwimLaneType.toWidgetViewHolderType(): WidgetViewHolderType = when (this) {
        SwimLaneType.NORMAL -> WidgetViewHolderType.STANDARD
        SwimLaneType.LARGE -> WidgetViewHolderType.LARGE
    }
}
