package tv.accedo.dishonstream2.ui.main.home.component.swimlane

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.SwimlaneLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneType
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget
import tv.accedo.dishonstream2.ui.base.viewholder.itemdecoration.HorizontalSpaceItemDecoration
import tv.accedo.dishonstream2.ui.base.viewholder.itemdecoration.VerticalSpaceItemDecoration
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder


class SwimLane @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val ORIENTATION_HORIZONTAL = 0
        private const val ORIENTATION_VERTICAL = 1
    }

    private var orientation: Int = ORIENTATION_VERTICAL
    private var itemHeight = context.resources.getDimensionPixelSize(R.dimen._300dp)
    private var itemWidth = ViewGroup.LayoutParams.MATCH_PARENT
    private var itemSpacing = context.resources.getDimensionPixelSize(R.dimen._20dp)
    private var swimLaneAdapter: SwimLaneAdapter? = null

    private val binding = SwimlaneLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        context.theme.obtainStyledAttributes(attrs, R.styleable.SwimLane, defStyleAttr, 0).run {
            try {
                orientation = this.getInt(R.styleable.SwimLane_android_orientation, ORIENTATION_VERTICAL)
                itemHeight = this.getDimensionPixelSize(
                    R.styleable.SwimLane_swimLaneItemHeight,
                    if (orientation == ORIENTATION_VERTICAL)
                        context.resources.getDimensionPixelSize(R.dimen._300dp)
                    else
                        ViewGroup.LayoutParams.MATCH_PARENT

                )

                itemWidth = this.getDimensionPixelSize(
                    R.styleable.SwimLane_swimLaneItemWidth,
                    if (orientation == ORIENTATION_HORIZONTAL)
                        context.resources.getDimensionPixelSize(R.dimen._300dp)
                    else
                        ViewGroup.LayoutParams.MATCH_PARENT
                )

                itemSpacing = this.getDimensionPixelSize(
                    R.styleable.SwimLane_itemSpacing,
                    context.resources.getDimensionPixelSize(R.dimen._20dp)
                )

            } finally {
                this.recycle()
            }
        }
    }

    fun setDataSource(dataSource: DataSource) {
        swimLaneAdapter = SwimLaneAdapter(
            dataSource,
            itemWidth,
            itemHeight,
            isLargeSwimLane = dataSource.type() == SwimLaneType.LARGE
        )

        val isVerticalSwimLane = orientation == ORIENTATION_VERTICAL
        val layoutDirection = if (isVerticalSwimLane) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL
        binding.root.layoutManager = LinearLayoutManager(context, layoutDirection, false)
        binding.root.adapter = swimLaneAdapter
        binding.root.setItemViewCacheSize(20)
        binding.root.addItemDecoration(
            if (isVerticalSwimLane) VerticalSpaceItemDecoration(itemSpacing)
            else HorizontalSpaceItemDecoration(itemSpacing)
        )
    }

    fun setItemWidth(width: Int) {
        itemWidth = width
        swimLaneAdapter?.updateItemWidth(itemWidth)
    }

    fun setItemHeight(height: Int) {
        itemHeight = height
        swimLaneAdapter?.updateItemHeight(itemHeight)
    }

    abstract class DataSource {
        abstract fun type(): SwimLaneType

        abstract fun getWidgets(): List<Widget>

        abstract fun onCreateWidgetViewHolder(parent: ViewGroup, widget: Widget): WidgetViewHolder

        abstract fun onBindWidgetViewHolder(
            widgetViewHolder: WidgetViewHolder,
            widget: Widget,
            position: Int,
            itemsCount: Int,
            isLargeSwimLane: Boolean
        )
    }
}