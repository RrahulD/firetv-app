package tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.carousel

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.extensions.consumeLeftKeyEvent
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder

class ResidentHomeHeroCarouselAdapter(
    private val dataSource: ResidentHomeScreenCarouselViewHolder.DataSource
) : RecyclerView.Adapter<WidgetViewHolder>() {

    var carouselBackgroundLoader: ((String?) -> Unit)? = null

    private val carouselWidgets = dataSource.getWidgets()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        return dataSource.onCreateWidgetViewHolder(parent, carouselWidgets[viewType])
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        dataSource.onBindWidgetViewHolder(holder, carouselWidgets[position], position, itemCount, carouselBackgroundLoader)
        holder.focusView.nextFocusUpId = R.id.home
        if (position == 0) holder.focusView.consumeLeftKeyEvent()
        if (position == itemCount - 1) holder.focusView.consumeRightKeyEvent()
    }

    override fun onViewAttachedToWindow(holder: WidgetViewHolder) {
        holder.attach()
    }

    override fun onViewDetachedFromWindow(holder: WidgetViewHolder) {
        holder.detach()
    }

    override fun getItemCount(): Int = carouselWidgets.size

    override fun getItemViewType(position: Int): Int = position
}