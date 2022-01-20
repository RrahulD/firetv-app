package tv.accedo.dishonstream2.ui.main.ondemand

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.model.search.SearchResultsData
import tv.accedo.dishonstream2.domain.model.vod.Movie
import tv.accedo.dishonstream2.domain.model.vod.Show
import tv.accedo.dishonstream2.domain.model.vod.VODContent
import tv.accedo.dishonstream2.domain.usecase.vod.GetPopularMoviesUseCase
import tv.accedo.dishonstream2.domain.usecase.vod.GetPopularShowsUseCase
import tv.accedo.dishonstream2.domain.usecase.vod.GetTrendingContentsUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class OnDemandViewModel(
    private val getTrendingContentsUseCase: GetTrendingContentsUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getPopularShowsUseCase: GetPopularShowsUseCase
) : BaseViewModel() {

    private val onDemandResultsMutableLiveData: MutableLiveData<List<SearchResultsData>> =
        MutableLiveData()
    val onDemandResultsLiveData: LiveData<List<SearchResultsData>> =
        onDemandResultsMutableLiveData


    fun fetchOnDemandData() {
        viewModelScope.launch(Dispatchers.Default) {
            val result: MutableList<SearchResultsData> = ArrayList()
            val popularContents: List<VODContent> = getTrendingContentsUseCase()
            result.add(SearchResultsData(TITLE_TRENDING_CONTENTS, popularContents))
            val popularShows: List<Show> = getPopularShowsUseCase(MAX_CONTENTS_COUNT, 0)
            result.add(SearchResultsData(TITLE_POPULAR_SHOWS, popularShows))
            val popularMovies: List<Movie> = getPopularMoviesUseCase(MAX_CONTENTS_COUNT, 0)
            result.add(SearchResultsData(TITLE_POPULAR_MOVIES, popularMovies))
            onDemandResultsMutableLiveData.postValue(result)
        }
    }

    companion object {
        private const val MAX_CONTENTS_COUNT = 100
        private const val TITLE_TRENDING_CONTENTS = "Trending Contents"
        private const val TITLE_POPULAR_SHOWS = "Popular Shows"
        private const val TITLE_POPULAR_MOVIES = "Popular Movies"
    }

}