package tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory

import android.view.LayoutInflater
import android.view.ViewGroup
import tv.accedo.dishonstream2.databinding.*
import tv.accedo.dishonstream2.domain.model.home.template.widget.*
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.error.ErrorWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel.LiveChannelWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo.MoreInfoWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.notification.NotificationWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.staticad.StaticAdWidgetViewHolder

class LargeWidgetViewHolderFactory {

    fun getWidgetViewHolder(
        widget: Widget,
        baseFragment: BaseFragment,
        parent: ViewGroup,
        isZoomEnabled: Boolean
    ): WidgetViewHolder {
        val layoutInflater = LayoutInflater.from(baseFragment.requireContext())
        val fragmentManager = baseFragment.requireActivity().supportFragmentManager
        return when (widget) {
            is LiveChannelWidget ->
                LiveChannelWidgetViewHolder(
                    WidgetLiveChannelLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ),
                    fragmentManager,
                    baseFragment
                ).also { if (isZoomEnabled) it.setZoom() }

            is MoreInfoWidget ->
                MoreInfoWidgetViewHolder(
                    WidgetMoreInfoLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ),
                    fragmentManager
                ).also { if (isZoomEnabled) it.setZoom() }

            is NotificationWidget ->
                NotificationWidgetViewHolder(
                    WidgetNotificationLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ),
                    fragmentManager
                ).also { if (isZoomEnabled) it.setZoom() }

            is StaticAdWidget ->
                StaticAdWidgetViewHolder(
                    WidgetStaticAdLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ),
                    fragmentManager
                ).also { if (isZoomEnabled) it.setZoom() }

            else ->
                ErrorWidgetViewHolder(
                    WidgetErrorLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                ).also { if (isZoomEnabled) it.setZoom() }
        }
    }
}