package tv.accedo.dishonstream2.ui.main.home.template.guest

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.GuestHomeTemplateLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.GuestHomeTemplate
import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneType
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.swimlane.SwimLane
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderType
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.util.WidgetUIHelper
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class GuestHomeTemplateScreen(
    private val baseFragment: BaseFragment,
    private val guestHomeTemplate: GuestHomeTemplate
) : KoinComponent {
    private val layoutInflater = LayoutInflater.from(baseFragment.requireContext())
    private val binding = GuestHomeTemplateLayoutBinding.inflate(layoutInflater)
    private val widgetViewHolderFactory: WidgetViewHolderFactory by inject()
    private val widgetUIHelper: WidgetUIHelper by inject()
    private val downKeyEventConsumer = View.OnKeyListener { _, keyEvent, _ -> keyEvent == KeyEvent.KEYCODE_DPAD_DOWN }
    private val themeManager: ThemeManager by inject()

    val view = binding.root

    init {
        populateTemplate()
    }

    private fun populateTemplate() {
        binding.leftColumn.setDataSource(object : SwimLane.DataSource() {
            val swimLaneType = guestHomeTemplate.leftColumn.type

            override fun type(): SwimLaneType = swimLaneType

            override fun getWidgets() = guestHomeTemplate.leftColumn.widgets

            override fun onCreateWidgetViewHolder(parent: ViewGroup, widget: Widget): WidgetViewHolder {
                return widgetViewHolderFactory.getWidgetViewHolder(
                    widget, baseFragment, parent, swimLaneType.toWidgetViewHolderType()
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
                mayBePreventUnExpectedFocusChanges(position, itemsCount, widgetViewHolder)
                addGuestTemplateSelectorBackground(widgetViewHolder.focusView)
            }
        })


        binding.centerColumn.setDataSource(object : SwimLane.DataSource() {
            val swimLaneType = guestHomeTemplate.centerColumn.type

            override fun type(): SwimLaneType = swimLaneType

            override fun getWidgets(): List<Widget> = guestHomeTemplate.centerColumn.widgets

            override fun onCreateWidgetViewHolder(parent: ViewGroup, widget: Widget): WidgetViewHolder {
                return widgetViewHolderFactory.getWidgetViewHolder(
                    widget, baseFragment, parent, swimLaneType.toWidgetViewHolderType()
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
                mayBePreventUnExpectedFocusChanges(position, itemsCount, widgetViewHolder)
                addGuestTemplateSelectorBackground(widgetViewHolder.focusView)
            }

        })

        binding.rightColumn.setDataSource(object : SwimLane.DataSource() {
            val swimLaneType = guestHomeTemplate.rightColumn.type

            override fun type(): SwimLaneType = swimLaneType

            override fun getWidgets(): List<Widget> = guestHomeTemplate.rightColumn.widgets

            override fun onCreateWidgetViewHolder(parent: ViewGroup, widget: Widget): WidgetViewHolder {
                return widgetViewHolderFactory.getWidgetViewHolder(
                    widget, baseFragment, parent, swimLaneType.toWidgetViewHolderType()
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
                mayBePreventUnExpectedFocusChanges(position, itemsCount, widgetViewHolder)
                addGuestTemplateSelectorBackground(widgetViewHolder.focusView)
            }
        })
    }

    private fun addGuestTemplateSelectorBackground(focusView: View) {
        focusView.background = themeManager.getWidgetBackgroundDrawable()
            ?: ContextCompat.getDrawable(focusView.context, R.drawable.widget_guest_template_selector_background)
    }

    private fun mayBePreventUnExpectedFocusChanges(position: Int, itemsCount: Int, widgetViewHolder: WidgetViewHolder) {
        if (position == 0)
            widgetViewHolder.focusView.nextFocusUpId = R.id.home

        // when vertical multiple recycler views are arranged in a row, there were some unexpected focus jumps
        // when on last item of the vertical recycler view. this code is added to prevent that.
        if (position == itemsCount - 1)
            widgetViewHolder.focusView.setOnKeyListener(downKeyEventConsumer)
    }

    private fun SwimLaneType.toWidgetViewHolderType(): WidgetViewHolderType = when (this) {
        SwimLaneType.NORMAL -> WidgetViewHolderType.STANDARD
        SwimLaneType.LARGE -> WidgetViewHolderType.LARGE
    }
}