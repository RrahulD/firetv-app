package tv.accedo.dishonstream2.ui.main.home.component.swimlane

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder

class SwimLaneAdapter(
    private val dataSource: SwimLane.DataSource,
    private var itemWidth: Int,
    private var itemHeight: Int,
    private val isLargeSwimLane: Boolean
) : RecyclerView.Adapter<WidgetViewHolder>() {

    private val widgets = dataSource.getWidgets()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        return dataSource.onCreateWidgetViewHolder(parent, widgets[viewType])
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.itemView.layoutParams = ViewGroup.LayoutParams(itemWidth, itemHeight)
        dataSource.onBindWidgetViewHolder(holder, widgets[position], position, itemCount, isLargeSwimLane)
    }

    override fun getItemCount(): Int = widgets.size

    override fun getItemViewType(position: Int) = position

    override fun onViewAttachedToWindow(holder: WidgetViewHolder) {
        holder.attach()
    }

    override fun onViewDetachedFromWindow(holder: WidgetViewHolder) {
        holder.detach()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItemWidth(width: Int) {
        itemWidth = width
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItemHeight(height: Int) {
        itemHeight = height
        notifyDataSetChanged()
    }
}