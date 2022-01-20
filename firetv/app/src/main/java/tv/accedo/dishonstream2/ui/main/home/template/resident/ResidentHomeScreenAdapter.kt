package tv.accedo.dishonstream2.ui.main.home.template.resident

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.databinding.HomeScreenResidentCarouselLayoutBinding
import tv.accedo.dishonstream2.databinding.HomeScreenResidentSwimlaneLayoutBinding
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.util.WidgetUIHelper
import tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.ResidentHomeScreenItem
import tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.carousel.ResidentHomeScreenCarouselViewHolder
import tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.contentcontainer.ResidentHomeScreenSwimLaneViewHolder

@OptIn(KoinApiExtension::class)
class ResidentHomeScreenAdapter(
    private val homeScreenItems: List<ResidentHomeScreenItem>,
    private val baseFragment: BaseFragment,
    private val isZoomEnabled: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {

    var carouselBackgroundLoader: ((String?) -> Unit)? = null

    private val widgetViewHolderFactory: WidgetViewHolderFactory by inject()
    private val widgetUIHelper: WidgetUIHelper by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (homeScreenItems[viewType]) {
            is ResidentHomeScreenItem.HeroCarousel -> ResidentHomeScreenCarouselViewHolder(
                HomeScreenResidentCarouselLayoutBinding.inflate(layoutInflater, parent, false),
                baseFragment,
                widgetViewHolderFactory,
                widgetUIHelper,
                isZoomEnabled
            )
            is ResidentHomeScreenItem.SwimLane -> ResidentHomeScreenSwimLaneViewHolder(
                HomeScreenResidentSwimlaneLayoutBinding.inflate(layoutInflater, parent, false),
                baseFragment,
                widgetViewHolderFactory,
                widgetUIHelper,
                isZoomEnabled
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val homeScreenItem = homeScreenItems[position]
        if (holder is ResidentHomeScreenCarouselViewHolder && homeScreenItem is ResidentHomeScreenItem.HeroCarousel)
            holder.bind(homeScreenItem.carouselWidgets, carouselBackgroundLoader)
        else if (holder is ResidentHomeScreenSwimLaneViewHolder && homeScreenItem is ResidentHomeScreenItem.SwimLane)
            holder.bind(homeScreenItem.swimLaneContainer)
    }

    override fun getItemCount(): Int = homeScreenItems.size

    override fun getItemViewType(position: Int): Int = position
}