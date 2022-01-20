package tv.accedo.dishonstream2.ui.main.seeall

import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import timber.log.Timber
import tv.accedo.dishonstream2.databinding.LayoutSeeAllFragmentBinding
import tv.accedo.dishonstream2.domain.model.search.SearchEpgProgramData
import tv.accedo.dishonstream2.domain.model.vod.VODContent
import tv.accedo.dishonstream2.domain.usecase.home.Tab
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.MainFragment
import tv.accedo.dishonstream2.ui.main.search.PaginationScrollListener
import tv.accedo.dishonstream2.ui.main.search.SearchEpgAapter
import tv.accedo.dishonstream2.ui.main.search.SearchListAdapter
import tv.accedo.dishonstream2.ui.main.search.SeeAllViewModel
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.view.NavigationView

@OptIn(KoinApiExtension::class)
class SeeAllFragment : BaseFragment() {
    private lateinit var binding: LayoutSeeAllFragmentBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val seeAllViewModel: SeeAllViewModel by viewModel()
    private var selectedTab = Tab.NONE
    private var itemsLoading = false
    private var startIndex = AppConstants.Search.SEE_ALL_CARD_LIMIT
    private var total: Int = 0
    private val epgProgrammeData: MutableList<SearchEpgProgramData> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutSeeAllFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedTab = getNavigationView()?.currentSelectedTab() ?: Tab.NONE
        getNavigationView()?.unSelectLastTab()
        if (arguments?.containsKey(KEY_DATA_LIST) == true) {
            when (arguments?.getInt(KEY_CARD_TYPE, -1)) {
                AppConstants.SeeAll.TYPE_SEARCH_VOD_CARD -> {
                    val data: ArrayList<VODContent>? = arguments?.getParcelableArrayList(KEY_DATA_LIST)
                    Timber.tag(TAG).i("see all page vod size : ${data?.size}")
                    data?.let {
                        binding.seeAllRecyclerView.layoutManager = GridLayoutManager(context, MAX_ROW_ITEMS_VOD)
                        binding.seeAllRecyclerView.adapter =
                            SearchListAdapter(it, this, false)
                    }
                }
                AppConstants.SeeAll.TYPE_SEARCH_EPG_CARD -> {
                    val searchedText = arguments?.getString(KEY_SEARCHED_TEXT, "") ?: ""
                    val data: ArrayList<SearchEpgProgramData>? = arguments?.getParcelableArrayList(KEY_DATA_LIST)
                    total = arguments?.getInt(KEY_TOTAL_EPG_PROGRAMME_COUNT, 0) ?: 0
                    val layoutManager = GridLayoutManager(context, MAX_ROW_ITEMS_EPG)
                    binding.seeAllRecyclerView.layoutManager = layoutManager
                    binding.seeAllRecyclerView.adapter =
                        SearchEpgAapter(epgProgrammeData, total, this, false)
                    Timber.tag(TAG).i("see all page epg size : $total")
                    data?.let {
                        epgProgrammeData.addAll(it)
                        binding.seeAllRecyclerView.adapter?.notifyDataSetChanged()
                        if (total >= AppConstants.Search.SEE_ALL_CARD_LIMIT) {
                            binding.seeAllRecyclerView.addOnScrollListener(object :
                                PaginationScrollListener(layoutManager) {
                                override fun loadMoreItems() {
                                    binding.progressBar.show()
                                    Timber.tag(TAG).i("Load more items pagination")
                                    itemsLoading = true
                                    seeAllViewModel.getProgrammeInfo(
                                        searchedText,
                                        total,
                                        startIndex,
                                        startIndex + ITEMS_COUNT_FOR_PAGINATION
                                    )
                                }

                                override val isLastPage: Boolean
                                    get() = startIndex >= total
                                override val isLoading: Boolean
                                    get() = itemsLoading

                            })
                        }
                    }
                }
                else -> {
                    //No need to implement
                }
            }
            binding.seeAllRecyclerView.requestFocus()
        }
        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            if (enabled) binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
        }

        seeAllViewModel.searchEpgResultLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                epgProgrammeData.addAll(it)
                binding.seeAllRecyclerView.adapter?.notifyItemRangeChanged(startIndex, epgProgrammeData.size)
            }
            binding.progressBar.hide()
            itemsLoading = false
            startIndex += ITEMS_COUNT_FOR_PAGINATION
        }
    }

    private fun getNavigationView(): NavigationView? {
        return getMainFragment()?.getNavigationView()
    }

    private fun getMainFragment(): MainFragment? {
        return if (parentFragment is MainFragment) (parentFragment as MainFragment) else null
    }

    override fun onBackPressed(): Boolean {
        getNavigationView()?.requestFocusOnTab(selectedTab)
        getMainFragment()?.onTabSelected(selectedTab)
        return true
    }

    companion object {
        const val TAG = "SeeAll : "
        private const val KEY_DATA_LIST = "dataList"
        private const val KEY_TOTAL_EPG_PROGRAMME_COUNT = "totalEpgProgrammeCount"
        private const val KEY_SEARCHED_TEXT = "searchedText"
        private const val KEY_CARD_TYPE = "cardType"
        private const val MAX_ROW_ITEMS_VOD = 7
        private const val MAX_ROW_ITEMS_EPG = 4
        private const val ITEMS_COUNT_FOR_PAGINATION = 20
        fun newInstance(data: List<Any>?, cardType: Int, total: Int?, searchText: String?): SeeAllFragment {
            return SeeAllFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_CARD_TYPE, cardType)
                    putParcelableArrayList(KEY_DATA_LIST, data as ArrayList<out Parcelable>)
                    if (AppConstants.SeeAll.TYPE_SEARCH_EPG_CARD == cardType) {
                        putInt(KEY_TOTAL_EPG_PROGRAMME_COUNT, total ?: 0)
                        putString(KEY_SEARCHED_TEXT, searchText)
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && parentFragment is MainFragment
            && (parentFragment as MainFragment).isHeaderBarFocussed()
        ) {
            binding.seeAllRecyclerView.getChildAt(0)?.requestFocus()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && isFirstRowItemFocussed()) {
            getNavigationView()?.requestFocusOnTab(Tab.SEARCH)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun isFirstRowItemFocussed(): Boolean {
        return binding.seeAllRecyclerView.getChildLayoutPosition(binding.seeAllRecyclerView.focusedChild) in 0 until MAX_ROW_ITEMS_VOD
    }
}