package tv.accedo.dishonstream2.ui.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.LayoutSeeAllViewHolderBinding
import tv.accedo.dishonstream2.databinding.WidgetLiveChannelLayoutBinding
import tv.accedo.dishonstream2.databinding.WidgetMoreInfoLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.LiveChannelWidget
import tv.accedo.dishonstream2.domain.model.search.SearchEpgProgramData
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel.LiveChannelWidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo.MoreInfoWidgetViewHolder
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class SearchEpgAapter(
    private val data: List<SearchEpgProgramData>,
    private val total: Int?,
    private val baseFragment: BaseFragment,
    private val isZoomEnabled: Boolean,
    private var onSearchItemClick: ((Any) -> Unit)? = null,
    private var showSeeAll: Boolean = false,
    private var searchText: String? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {
    private val themeManager: ThemeManager by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when {
            showSeeAll && total ?: 0 >= AppConstants.Search.SEE_ALL_CARD_LIMIT && viewType == AppConstants.Search.SEE_ALL_CARD_LIMIT - 1 -> {
                SeeAllViewHolder(
                    LayoutSeeAllViewHolderBinding.inflate(inflater, parent, false),
                    baseFragment.parentFragmentManager
                )
            }
            data[viewType].program.isLive() -> {
                LiveChannelWidgetViewHolder(
                    WidgetLiveChannelLayoutBinding.inflate(inflater, parent, false),
                    baseFragment.requireActivity().supportFragmentManager,
                    baseFragment
                )
            }
            else -> {
                MoreInfoWidgetViewHolder(
                    WidgetMoreInfoLayoutBinding.inflate(inflater, parent, false),
                    baseFragment.requireActivity().supportFragmentManager
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LiveChannelWidgetViewHolder -> {
                if (showSeeAll && position == 0) {
                    holder.binding.container.nextFocusLeftId = R.id.key_x
                }
                themeManager.getWidgetBackgroundDrawable()?.let { holder.focusView.background = it }
                holder.onSearchItemClick = onSearchItemClick
                holder.populateSearchData(
                    LiveChannelWidget("", data[position].program.channelId),
                    position == data.size.minus(1), !showSeeAll
                )
            }
            is MoreInfoWidgetViewHolder -> {
                if (showSeeAll && position == 0) {
                    holder.binding.container.nextFocusLeftId = R.id.key_x
                }
                themeManager.getWidgetBackgroundDrawable()?.let { holder.focusView.background = it }
                holder.populate(
                    data[position],
                    onSearchItemClick,
                    position == data.size.minus(1), !showSeeAll
                )
            }
            is SeeAllViewHolder -> {
                holder.populate(themeManager, AppConstants.SeeAll.TYPE_SEARCH_EPG_CARD, data, total, searchText)
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