package tv.accedo.dishonstream2.ui.main.search

import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.LayoutButtonsBinding
import tv.accedo.dishonstream2.databinding.SearchFragmentBinding
import tv.accedo.dishonstream2.domain.model.search.SearchResultsData
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.model.vod.Movie
import tv.accedo.dishonstream2.domain.model.vod.Show
import tv.accedo.dishonstream2.domain.usecase.home.Tab
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.MainFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager
import tv.accedo.dishonstream2.ui.view.NavigationView


@OptIn(KoinApiExtension::class)
class SearchFragment : BaseFragment(), View.OnClickListener {
    private val themeManager: ThemeManager by inject()
    private lateinit var binding: SearchFragmentBinding
    private val searchViewModel: SearchViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private var inputConnection: InputConnection? = null
    private val searchListResults: MutableList<SearchResultsData> = ArrayList()
    private var popularContent: SearchResultsData? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SearchFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFocusDownFromMenu()
        updateBorderColorBasedOnTheme()
        binding.searchListRecyclerView.adapter =
            SearchItemAdapter(
                searchListResults,
                this,
                sharedAppViewModel.largeFontEnabledLiveData.value ?: false
            ) { clickedItem ->
                val title: String? = when (clickedItem) {
                    is Movie -> clickedItem.title
                    is Show -> clickedItem.title
                    is Program -> clickedItem.name
                    else -> null
                }
                title?.let {
                    searchViewModel.setRecentSearchFields(it)
                }
            }
        binding.deleteRecentSearchBtn.setOnClickListener(this)
        inputConnection = binding.searchEditText.onCreateInputConnection(EditorInfo())
        binding.keyboardLayout.inputConnection = inputConnection
        binding.keyboardLayout.requestFocus()
        binding.searchEditText.addTextChangedListener {
            val searchField = it?.toString()
            searchField?.let {
                if (searchField.length > 2) {
                    Timber.tag(AppConstants.Search.TAG).i("perform search : $searchField")
                    showNoSearchError(false)
                    clearSearch()
                    binding.progressBar.show()
                    searchViewModel.performSearch(searchField)
                } else {
                    searchViewModel.cancelSearchJob()
                    showPopularContent()
                }
            }

        }
        searchViewModel.recentSearchLiveData.observe(viewLifecycleOwner) { recentSearches ->
            binding.recentSearchBtnLayout.removeAllViews()
            val lineaLayout = LinearLayout(requireContext())
            lineaLayout.layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen._50dp).toInt())
            lineaLayout.orientation = LinearLayout.HORIZONTAL
            recentSearches.forEachIndexed { index, text ->
                val btn = getBtnView(text)
                if (index == 0) {
                    btn.nextFocusLeftId = R.id.key_l
                }
                lineaLayout.addView(btn)
            }
            binding.recentSearchBtnLayout.addView(lineaLayout)
            if (recentSearches.isNotEmpty()) {
                binding.recentSearchesText.show()
                binding.recentSearchParent.show()
            } else {
                binding.recentSearchesText.hide()
                binding.recentSearchParent.hide()
            }
        }

        searchViewModel.searchResultLiveData.observe(viewLifecycleOwner) { searchResults ->
            showNoSearchError(false)
            searchListResults.clear()
            if (!searchResults.isNullOrEmpty()) {
                searchListResults.addAll(searchResults)
                binding.searchListRecyclerView?.adapter?.notifyDataSetChanged()
            }
            binding.progressBar.hide()
        }

        searchViewModel.searchErrorLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.hide()
            showNoSearchResultsError(
                when (it) {
                    SearchViewModel.NO_SEARCH_RESULTS -> getString(R.string.search_error_description_text).format(binding.searchEditText.text?.toString())
                    SearchViewModel.INTERNET_NOT_CONNECTED_ERROR -> getString(R.string.internet_connection_error)
                    else -> getString(R.string.generic_error)
                }
            )

        }

        searchViewModel.popularContentLiveData.observe(viewLifecycleOwner) {
            showNoSearchError(false)
            Timber.tag(AppConstants.Search.TAG).i("popular content size : ${it.data.size}")
            searchListResults.clear()
            if (!it.data.isNullOrEmpty()) {
                popularContent = it
                searchListResults.add(it)
            }
            binding.searchListRecyclerView?.adapter?.notifyDataSetChanged()
            binding.progressBar.hide()
        }
        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            if (enabled) setZoom()
        }
        binding.searchEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                requireActivity()?.findViewById<TextView>(R.id.search)?.requestFocus()
            }

        }
    }

    private fun getBtnView(btnText: String): AppCompatButton {
        val binding = LayoutButtonsBinding.inflate(layoutInflater)
        binding.recentSearchBtns.height = resources.getDimension(R.dimen._40dp).toInt()
        binding.recentSearchBtns.text = btnText
        binding.recentSearchBtns.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (sharedAppViewModel.largeFontEnabledLiveData.value == true) 19f else 15f
        )
        binding.recentSearchBtns.setOnClickListener(this)
        themeManager.getSettingOptionsBackgroundDrawable()?.let {
            binding.recentSearchBtns.background = it
        }
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.recentSearchBtns -> {
                val selectedBtn: AppCompatButton = v as AppCompatButton
                binding.searchEditText.setText("")
                inputConnection?.commitText(selectedBtn.text, selectedBtn.text.length)
            }
            binding.deleteRecentSearchBtn.id -> {
                searchViewModel.clearRecentSearches()
                binding.keyboardLayout.requestFocus()
            }
            else -> {
                // No action required
            }
        }
    }

    private fun showPopularContent() {
        binding.progressBar.hide()
        showNoSearchError(false)
        searchListResults.clear()
        popularContent?.let {
            if (!it.data.isNullOrEmpty()) {
                searchListResults.add(it)
            }
            binding.searchListRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    private fun showNoSearchResultsError(msg: String) {
        clearSearch()
        binding.errorDescription.text = msg
        showNoSearchError(true)
    }

    private fun updateBorderColorBasedOnTheme() {
        themeManager.getSettingOptionsBackgroundDrawable()?.let {
            binding.deleteRecentSearchBtn.background = it
        }
    }

    private fun setFocusDownFromMenu() {
        binding.keyboardLayout.binding.keyA.id.let {
            requireActivity()?.findViewById<TextView>(R.id.home)?.nextFocusDownId = it
            requireActivity()?.findViewById<TextView>(R.id.tv_guide)?.nextFocusDownId = it
            requireActivity()?.findViewById<TextView>(R.id.search)?.nextFocusDownId = it
            requireActivity()?.findViewById<TextView>(R.id.settings)?.nextFocusDownId = it
            requireActivity()?.findViewById<TextView>(R.id.on_demand)?.nextFocusDownId = it
        }
    }

    private fun clearSearch() {
        searchListResults.clear()
        binding.searchListRecyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun showNoSearchError(show: Boolean) {
        if (show) {
            binding.recyclerViewParent.hide()
            binding.warningImage.show()
            binding.errorTitle.show()
            binding.errorDescription.show()
        } else {
            binding.recyclerViewParent.show()
            binding.warningImage.hide()
            binding.errorTitle.hide()
            binding.errorDescription.hide()
        }
    }

    private fun setZoom() {
        binding.searchHeadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
        binding.searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        binding.recentSearchesText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        binding.deleteRecentSearchBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        (binding.keyboardLayout.layoutParams as ViewGroup.LayoutParams).width =
            binding.root.resources.getDimension(R.dimen._280dp).toInt()
        (binding.searchEditText.layoutParams as ViewGroup.MarginLayoutParams).marginStart =
            binding.root.resources.getDimension(R.dimen._340dp).toInt()
        (binding.searchItemsParent.layoutParams as ViewGroup.MarginLayoutParams).marginStart =
            binding.root.resources.getDimension(R.dimen._340dp).toInt()
        binding.keyboardLayout.setZoom()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && isMoveFocusToSearchReq()) {
            getNavigationView()?.requestFocusOnTab(Tab.SEARCH)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun isMoveFocusToSearchReq(): Boolean {
        val focussedPos = binding.searchListRecyclerView.getChildLayoutPosition(binding.searchListRecyclerView.focusedChild)
        if (focussedPos == 0 && binding.recentSearchesText.visibility == View.GONE) {
            return true
        }
        return false
    }

    private fun getNavigationView(): NavigationView? {
        return if (parentFragment is MainFragment) (parentFragment as MainFragment).getNavigationView() else null
    }

    companion object {
        fun newInstance() = SearchFragment()
    }

}