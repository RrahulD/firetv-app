package tv.accedo.dishonstream2.ui.main.home.component.widget.base.util

import tv.accedo.dishonstream2.domain.model.home.template.widget.*
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.game.GameWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel.LiveChannelCarouselWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel.LiveChannelWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo.MoreInfoCarouselWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo.MoreInfoWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.notification.NotificationWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.staticad.StaticAdCarouselWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.staticad.StaticAdWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.weather.WeatherWidgetViewHolder

class WidgetUIHelper {
    fun populateWidget(
        widget: Widget,
        widgetViewHolder: WidgetViewHolder,
        isLargeSwimLane: Boolean,
        isZoomEnabled: Boolean = false
    ) {
        when {
            widget is LiveChannelWidget && widgetViewHolder is LiveChannelWidgetViewHolder ->
                widgetViewHolder.populate(widget)

            widget is StaticAdWidget && widgetViewHolder is StaticAdWidgetViewHolder ->
                widgetViewHolder.populate(widget, isLargeSwimLane)

            widget is NotificationWidget && widgetViewHolder is NotificationWidgetViewHolder ->
                widgetViewHolder.populate(widget)

            widget is MoreInfoWidget && widgetViewHolder is MoreInfoWidgetViewHolder ->
                widgetViewHolder.populate(widget)

            widgetViewHolder is WeatherWidgetViewHolder ->
                widgetViewHolder.populate()

            widgetViewHolder is GameWidgetViewHolder && widget is GameWidget ->
                widgetViewHolder.populate(widget)

            else -> {
                // do nothing
            }
        }
    }

    fun populateCarouselWidget(
        widget: Widget,
        widgetViewHolder: WidgetViewHolder,
        carouselBackgroundLoader: ((String?) -> Unit)?
    ) {
        when {
            widget is LiveChannelWidget && widgetViewHolder is LiveChannelCarouselWidgetViewHolder ->
                widgetViewHolder.populate(widget, carouselBackgroundLoader)

            widget is MoreInfoWidget && widgetViewHolder is MoreInfoCarouselWidgetViewHolder ->
                widgetViewHolder.populate(widget, carouselBackgroundLoader)

            widget is StaticAdWidget && widgetViewHolder is StaticAdCarouselWidgetViewHolder ->
                widgetViewHolder.populate(widget, carouselBackgroundLoader)
        }
    }
}