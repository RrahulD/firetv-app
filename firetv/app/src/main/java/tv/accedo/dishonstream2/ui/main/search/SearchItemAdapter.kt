package tv.accedo.dishonstream2.ui.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import tv.accedo.dishonstream2.databinding.LayoutSearchItemListViewBinding
import tv.accedo.dishonstream2.domain.model.search.SearchResultsData
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.ui.base.BaseFragment

@OptIn(KoinApiExtension::class)
class SearchItemAdapter(
    private val data: List<SearchResultsData>,
    private val baseFragment: BaseFragment,
    private val isZoomEnabled: Boolean,
    private var onSearchItemClick: ((Any) -> Unit)? = null
) : RecyclerView.Adapter<SearchItemViewHolder>(), KoinComponent {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchItemViewHolder(
            LayoutSearchItemListViewBinding.inflate(inflater, parent, false),
            baseFragment
        )
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        if (data[position].title == AppConstants.Search.TITLE_SEARCH_EPG_RAIL) {
            holder.bindEpgData(
                data[position],
                isZoomEnabled,
                onSearchItemClick
            )
        } else {
            holder.bindVodData(
                data[position],
                isZoomEnabled,
                onSearchItemClick
            )
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}