package tv.accedo.dishonstream2.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.search.SearchEpgProgramData
import tv.accedo.dishonstream2.domain.model.search.SearchResultsData
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.usecase.program.GetProgramInfoUseCase
import tv.accedo.dishonstream2.domain.usecase.search.GetRecentSearchFieldsUseCase
import tv.accedo.dishonstream2.domain.usecase.search.GetSearchProgrameResultsUseCase
import tv.accedo.dishonstream2.domain.usecase.search.SetRecentSearchFieldsUseCase
import tv.accedo.dishonstream2.domain.usecase.vod.GetPopularContentUseCase
import tv.accedo.dishonstream2.domain.usecase.vod.SearchVODContentUseCase
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class SearchViewModel(
    private val getRecentSearchFieldsUseCase: GetRecentSearchFieldsUseCase,
    private val setRecentSearchFieldsUseCase: SetRecentSearchFieldsUseCase,
    private val getPopularContentUseCase: GetPopularContentUseCase,
    private val searchVODContentUseCase: SearchVODContentUseCase,
    private val getSearchProgrameResultsUseCase: GetSearchProgrameResultsUseCase,
    private val getProgramInfoUseCase: GetProgramInfoUseCase
) : BaseViewModel() {

    private val recentSearchMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val recentSearchLiveData: LiveData<List<String>> = recentSearchMutableLiveData

    private val searchResultMutableLiveData: MutableLiveData<List<SearchResultsData>> =
        MutableLiveData()
    val searchResultLiveData: LiveData<List<SearchResultsData>> =
        searchResultMutableLiveData

    private val popularContentMutableLiveData: MutableLiveData<SearchResultsData> = MutableLiveData()
    val popularContentLiveData: LiveData<SearchResultsData> = popularContentMutableLiveData

    private val searchErrorMutableLiveData: MutableLiveData<Int> = MutableLiveData()
    val searchErrorLiveData: LiveData<Int> = searchErrorMutableLiveData
    private var job: Job? = null

    init {
        getRecentSearchFields()
        getPopularContent()
    }

    fun cancelSearchJob() {
        job?.cancel()
    }

    fun performSearch(searchQuery: String) {
        cancelSearchJob()
        job = viewModelScope.launch {
            getSearchResults(searchQuery)
        }
    }

    fun setRecentSearchFields(value: String) {
        viewModelScope.launch {
            try {
                val recentSearches = getRecentSearchFieldsUseCase()
                val newRecentSearch = LinkedHashSet<String>()
                newRecentSearch.add(value)
                if (recentSearches.isNullOrEmpty()) {
                    setRecentSearchFieldsUseCase(newRecentSearch)
                } else {
                    newRecentSearch.addAll(recentSearches.take(MAX_RECENT_SEARCH_ITEM_INDEX))
                    setRecentSearchFieldsUseCase(newRecentSearch)
                }
                getRecentSearchFields()
            } catch (ex: Exception) {
                Timber.tag(AppConstants.Search.TAG).e("Exception in setRecentSearchFields() : $ex")
            }
        }
    }

    private fun getRecentSearchFields() {
        viewModelScope.launch {
            try {
                getRecentSearchFieldsUseCase()?.let {
                    recentSearchMutableLiveData.value = it.toList()
                }

            } catch (ex: Exception) {
                Timber.tag(AppConstants.Search.TAG).e("Exception in getRecentSearchFields() : $ex")
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            try {
                setRecentSearchFieldsUseCase(LinkedHashSet())
                getRecentSearchFields()
            } catch (ex: Exception) {
                Timber.tag(AppConstants.Search.TAG).e("Exception in clearRecentSearches() : $ex")
            }
        }
    }

    private suspend fun getSearchResults(searchQuery: String) {
        supervisorScope {
            val epgResult: Pair<List<Program>, Int> =
                getSearchProgrameResultsUseCase(searchQuery, AppConstants.Search.SEE_ALL_CARD_LIMIT)
            val programmeList: List<SearchEpgProgramData> = fetchProgrammeInfo(epgResult.first)
            val vodResults = searchVODContentUseCase(searchQuery)
            Timber.tag(AppConstants.Search.TAG)
                .i("getSearchResults search found in Epg : ${epgResult.first.size}, vod : ${vodResults?.size}")
            if (!isActive) {
                return@supervisorScope
            }
            if (epgResult.first.isEmpty() && vodResults.isNullOrEmpty()) {
                searchErrorMutableLiveData.postValue(NO_SEARCH_RESULTS)
            } else {
                val result: MutableList<SearchResultsData> = ArrayList()
                if (programmeList.isNotEmpty()) {
                    result.add(
                        SearchResultsData(
                            AppConstants.Search.TITLE_SEARCH_EPG_RAIL,
                            programmeList,
                            epgResult.second,
                            searchQuery
                        )
                    )
                }
                if (vodResults.isNotEmpty()) {
                    result.add(SearchResultsData(AppConstants.Search.TITLE_SEARCH_VOD_RAIL, vodResults))
                }
                searchResultMutableLiveData.postValue(result)
            }
        }
    }

    private suspend fun fetchProgrammeInfo(programmeList: List<Program>): List<SearchEpgProgramData> =
        withContext(Dispatchers.Default) {
            val result: MutableList<Deferred<SearchEpgProgramData>> = ArrayList()
            programmeList.forEach {
                try {
                    result.add(async {
                        SearchEpgProgramData(it, getProgramInfoUseCase(it.echoStarId))
                    })
                } catch (e: Exception) {
                    Timber.tag(AppConstants.Search.TAG).e("Exception in epg fuzzySearch fetchProgrammeInfo : ${e.toString()}")
                }
            }
            val data: MutableList<SearchEpgProgramData> = ArrayList()
            data.addAll(result.awaitAll())
            data
        }

    private fun getPopularContent() {
        viewModelScope.launch {
            val popularContents = getPopularContentUseCase(MAX_POPULAR_CONTENTS)
            popularContentMutableLiveData.value = SearchResultsData(
                AppConstants.Search.TITLE_SEARCH_POPULAR_ON_DEMAND_RAIL, popularContents
            )
        }
    }

    companion object {
        const val NO_SEARCH_RESULTS = 1
        const val GENERIC_ERROR = 2
        const val INTERNET_NOT_CONNECTED_ERROR = 3
        private const val MAX_RECENT_SEARCH_ITEM_INDEX = 4
        private const val MAX_POPULAR_CONTENTS = 100
    }
}