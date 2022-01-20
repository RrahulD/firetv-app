package tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory

import android.view.LayoutInflater
import android.view.ViewGroup
import tv.accedo.dishonstream2.databinding.CarouselWidgetErrorLayoutBinding
import tv.accedo.dishonstream2.databinding.CarouselWidgetLiveChannelLayoutBinding
import tv.accedo.dishonstream2.databinding.CarouselWidgetMoreInfoLayoutBinding
import tv.accedo.dishonstream2.databinding.CarouselWidgetStaticAdLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.LiveChannelWidget
import tv.accedo.dishonstream2.domain.model.home.template.widget.MoreInfoWidget
import tv.accedo.dishonstream2.domain.model.home.template.widget.StaticAdWidget
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.error.ErrorCarouselWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel.LiveChannelCarouselWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo.MoreInfoCarouselWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.staticad.StaticAdCarouselWidgetViewHolder

class CarouselWidgetViewHolderFactory {

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
                LiveChannelCarouselWidgetViewHolder(
                    CarouselWidgetLiveChannelLayoutBinding.inflate(layoutInflater, parent, false),
                    fragmentManager
                ).also { if (isZoomEnabled) it.setZoom() }

            is MoreInfoWidget ->
                MoreInfoCarouselWidgetViewHolder(
                    CarouselWidgetMoreInfoLayoutBinding.inflate(layoutInflater, parent, false),
                    fragmentManager
                ).also { if (isZoomEnabled) it.setZoom() }

            is StaticAdWidget ->
                StaticAdCarouselWidgetViewHolder(
                    CarouselWidgetStaticAdLayoutBinding.inflate(layoutInflater, parent, false),
                    fragmentManager
                ).also { if (isZoomEnabled) it.setZoom() }

            else ->
                ErrorCarouselWidgetViewHolder(
                    CarouselWidgetErrorLayoutBinding.inflate(layoutInflater, parent, false)
                ).also { if (isZoomEnabled) it.setZoom() }
        }
    }
}