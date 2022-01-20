package tv.accedo.dishonstream2.domain.usecase.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.usecase.channel.GetEPGUseCase
import tv.accedo.dishonstream2.domain.util.AppConstants

class GetSearchProgrameResultsUseCase(
    private val getEPGUseCase: GetEPGUseCase,
) {
    suspend operator fun invoke(
        searchQuery: String,
        itemsCountToBeSearched: Int
    ): Pair<List<Program>, Int> = withContext(Dispatchers.Default) {
        val epgDeferred = async { getEPGUseCase() }
        val channelList = epgDeferred.await()
        Timber.tag(AppConstants.Search.TAG).i("getSearchResults Channel size: ${channelList.size}")
        val searchedChLiveProgramme: MutableList<Program> = ArrayList()
        var programmeList: MutableList<Program> = ArrayList()
        channelList.forEach { channel ->
            channel.programs.forEach {
                if (!isActive) {
                    return@withContext Pair(emptyList<Program>(), 0)
                }
                it.channelId = channel.serviceKey
                it.channelLogoUrl = channel.detail?.logoURL
                // if search query matches with a live channel then we'll add its live running programme at first place
                if (channel.name.contains(searchQuery, true) && it.isLive()) {
                    searchedChLiveProgramme.add(it)
                } else if (it.isLiveOrFuture()) {
                    programmeList.add(it)
                }
            }
        }
        Timber.tag(AppConstants.Search.TAG)
            .d("Total channel size : ${channelList.size}, programsAdded size : ${programmeList.size}")
        // perform fuzzy search on all programmes
        val programmesMatched: List<BoundExtractedResult<Program>> = FuzzySearch.extractTop(
            searchQuery,
            programmeList,
            {
                it.toString()
            }, programmeList.size, FUZZY_SEARCH_MATCH_SCORE_CUT_OFF
        )
        programmeList.clear()
        Timber.tag(AppConstants.Search.TAG).d("Total Programmes matched count : ${programmesMatched.size}")
        programmesMatched.forEachIndexed { index, items ->
            if (!isActive) {
                return@withContext Pair(emptyList<Program>(), 0)
            }
            if (index >= itemsCountToBeSearched) {
                return@forEachIndexed
            }
            Timber.tag(AppConstants.Search.TAG).d("Programme score : ${items.score}, title : ${items.referent.name}")
            programmeList.add(items.referent)
        }
        programmeList = programmeList.sortedBy { it.startTime }.toMutableList()
        // Adding matched channels live programs at the top of search result
        programmeList.addAll(0, searchedChLiveProgramme)
        Pair(programmeList, programmesMatched.size)
    }

    companion object {
        private const val FUZZY_SEARCH_MATCH_SCORE_CUT_OFF = 40
    }
}