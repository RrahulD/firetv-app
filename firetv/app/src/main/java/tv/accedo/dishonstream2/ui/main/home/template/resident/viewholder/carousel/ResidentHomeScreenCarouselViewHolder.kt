package tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.carousel

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import tv.accedo.dishonstream2.databinding.HomeScreenResidentCarouselLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderType
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.util.WidgetUIHelper
import tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.carousel.pageindicator.CirclePagerIndicatorDecoration

@OptIn(KoinApiExtension::class)
class ResidentHomeScreenCarouselViewHolder(
    private val binding: HomeScreenResidentCarouselLayoutBinding,
    private val baseFragment: BaseFragment,
    private val widgetViewHolderFactory: WidgetViewHolderFactory,
    private val widgetUIHelper: WidgetUIHelper,
    private val isZoomEnabled: Boolean
) : RecyclerView.ViewHolder(binding.root), KoinComponent {

    fun bind(carouselWidgets: List<Widget>, carouselBackgroundLoader: ((String?) -> Unit)?) {
        val adapter = ResidentHomeHeroCarouselAdapter(object : ResidentHomeScreenCarouselViewHolder.DataSource() {
            override fun getWidgets(): List<Widget> = carouselWidgets

            override fun onCreateWidgetViewHolder(parent: ViewGroup, widget: Widget): WidgetViewHolder {
                return widgetViewHolderFactory.getWidgetViewHolder(
                    widget, baseFragment, parent, WidgetViewHolderType.CAROUSEL, isZoomEnabled
                )
            }

            override fun onBindWidgetViewHolder(
                widgetViewHolder: WidgetViewHolder,
                widget: Widget,
                position: Int,
                itemsCount: Int,
                carouselBackgroundLoader: ((String?) -> Unit)?
            ) {
                widgetUIHelper.populateCarouselWidget(widget, widgetViewHolder, carouselBackgroundLoader)
            }
        })

        adapter.carouselBackgroundLoader = carouselBackgroundLoader
        binding.carouselList.adapter = adapter
        binding.carouselList.addItemDecoration(CirclePagerIndicatorDecoration())
        binding.carouselList.requestFocus()
    }

    abstract class DataSource {
        abstract fun getWidgets(): List<Widget>

        abstract fun onCreateWidgetViewHolder(parent: ViewGroup, widget: Widget): WidgetViewHolder

        abstract fun onBindWidgetViewHolder(
            widgetViewHolder: WidgetViewHolder,
            widget: Widget,
            position: Int,
            itemsCount: Int,
            carouselBackgroundLoader: ((String?) -> Unit)?
        )
    }
}