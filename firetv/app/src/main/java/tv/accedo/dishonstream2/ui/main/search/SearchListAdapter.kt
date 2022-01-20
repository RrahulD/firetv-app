package tv.accedo.dishonstream2.ui.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.LayoutSeeAllViewHolderBinding
import tv.accedo.dishonstream2.databinding.LayoutVodViewHolderBinding
import tv.accedo.dishonstream2.domain.model.vod.VODContent
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class SearchListAdapter(
    private val data: List<VODContent>,
    private val baseFragment: BaseFragment,
    private val isZoomEnabled: Boolean,
    private var onSearchItemClick: ((Any) -> Unit)? = null,
    private var showSeeAll: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {
    private val themeManager: ThemeManager by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (showSeeAll && data.size >= AppConstants.Search.SEE_ALL_CARD_LIMIT && viewType == AppConstants.Search.SEE_ALL_CARD_LIMIT - 1) {
            SeeAllViewHolder(
                LayoutSeeAllViewHolderBinding.inflate(inflater, parent, false),
                baseFragment.parentFragmentManager
            )
        } else {
            VODViewHolder(LayoutVodViewHolderBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SeeAllViewHolder) {
            holder.populate(themeManager, AppConstants.SeeAll.TYPE_SEARCH_VOD_CARD, data)
        } else if (holder is VODViewHolder) {
            holder.onSearchItemClick = onSearchItemClick
            holder.bind(data[position], baseFragment, themeManager, position == data.size.minus(1))
            if (showSeeAll && position == 0) {
                holder.binding.root.nextFocusLeftId = R.id.key_x
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}