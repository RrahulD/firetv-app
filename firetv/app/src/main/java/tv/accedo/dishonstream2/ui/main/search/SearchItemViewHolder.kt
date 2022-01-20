package tv.accedo.dishonstream2.ui.main.search

import android.util.TypedValue
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.databinding.LayoutSearchItemListViewBinding
import tv.accedo.dishonstream2.domain.model.search.SearchEpgProgramData
import tv.accedo.dishonstream2.domain.model.search.SearchResultsData
import tv.accedo.dishonstream2.domain.model.vod.VODContent
import tv.accedo.dishonstream2.ui.base.BaseFragment

class SearchItemViewHolder(
    private val binding: LayoutSearchItemListViewBinding,
    private val baseFragment: BaseFragment
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.searchItemRecyclerView.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
    }

    fun bindEpgData(
        searchData: SearchResultsData,
        isZoomEnabled: Boolean,
        onSearchItemClick: ((Any) -> Unit)? = null
    ) {
        binding.railTitle.text = searchData.title
        binding.railTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 22f else 18f)
        binding.searchItemRecyclerView.adapter =
            SearchEpgAapter(
                searchData.data as List<SearchEpgProgramData>,
                searchData.total,
                baseFragment,
                isZoomEnabled,
                onSearchItemClick,
                true, searchData.searchText
            )
    }

    fun bindVodData(
        searchData: SearchResultsData,
        isZoomEnabled: Boolean,
        onSearchItemClick: ((Any) -> Unit)? = null
    ) {
        binding.railTitle.text = searchData.title
        binding.railTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 22f else 18f)
        binding.searchItemRecyclerView.adapter =
            SearchListAdapter(
                searchData.data as List<VODContent>,
                baseFragment,
                isZoomEnabled,
                onSearchItemClick,
                true
            )
    }
}