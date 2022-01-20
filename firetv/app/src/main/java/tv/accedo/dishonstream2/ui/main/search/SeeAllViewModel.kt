package tv.accedo.dishonstream2.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.search.SearchEpgProgramData
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.domain.usecase.program.GetProgramInfoUseCase
import tv.accedo.dishonstream2.domain.usecase.search.GetSearchProgrameResultsUseCase
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.ui.base.BaseViewModel
import tv.accedo.dishonstream2.ui.main.seeall.SeeAllFragment

class SeeAllViewModel(
    private val getSearchProgrameResultsUseCase: GetSearchProgrameResultsUseCase,
    private val getProgramInfoUseCase: GetProgramInfoUseCase
) : BaseViewModel() {
    private var epgProgrammesList: List<Program>? = null
    private val searchEpgResultMutableLiveData: MutableLiveData<List<SearchEpgProgramData>> =
        MutableLiveData()
    val searchEpgResultLiveData: LiveData<List<SearchEpgProgramData>> =
        searchEpgResultMutableLiveData

    private suspend fun searchEpgContents(searchQuery: String, totalCount: Int): List<Program> =
        withContext(Dispatchers.Default) {
            val data: List<Program> = getSearchProgrameResultsUseCase(searchQuery, totalCount).first
            epgProgrammesList = data
            data
        }


    fun getProgrammeInfo(searchQuery: String, totalCount: Int, start: Int, end: Int) {
        viewModelScope.launch {
            Timber.tag(SeeAllFragment.TAG).i("getProgrammeInfo from : $start, to : $end")
            val count = epgProgrammesList?.size ?: searchEpgContents(searchQuery, totalCount).size
            val data = fetchProgrammeInfo(count, start, end)
            searchEpgResultMutableLiveData.value = data
        }
    }

    private suspend fun fetchProgrammeInfo(count: Int, start: Int, end: Int): List<SearchEpgProgramData> =
        withContext(Dispatchers.Default) {
            val result: MutableList<SearchEpgProgramData> = ArrayList()
            if (start >= count || count <= 0) {
                return@withContext result
            }
            for (i in start until count) {
                if (i >= end) {
                    return@withContext result
                }
                epgProgrammesList?.let {
                    var programInfo: ProgramInfo? = null
                    try {
                        programInfo = getProgramInfoUseCase(it[i].echoStarId)
                    } catch (e: Exception) {
                        Timber.tag(AppConstants.Search.TAG).e("Exception in epg fuzzySearch : ${e.toString()}")
                    }
                    Timber.tag(AppConstants.Search.TAG).e("Adding in list index : $i, start : $start, end : $end, count : $count")
                    result.add(SearchEpgProgramData(it[i], programInfo))
                }
            }
            result
        }
}