package tv.accedo.dishonstream2.ui.main.ondemand

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.model.vod.MovieDetails
import tv.accedo.dishonstream2.domain.model.vod.ShowDetails
import tv.accedo.dishonstream2.domain.model.vod.VODService
import tv.accedo.dishonstream2.domain.usecase.vod.GetEpisodeDetailsUseCase
import tv.accedo.dishonstream2.domain.usecase.vod.GetMovieDetailsUseCase
import tv.accedo.dishonstream2.domain.usecase.vod.GetShowDetailsCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class OnDemandDetailsViewModel(
    private val movieDetailsUseCase: GetMovieDetailsUseCase,
    private val showDetailsUseCase: GetShowDetailsCase,
    private val episodeDetailsUseCase: GetEpisodeDetailsUseCase
) : BaseViewModel() {

    private val movieDetailsMutableLiveData: MutableLiveData<MovieDetails> = MutableLiveData()
    val movieDetailsLiveData: LiveData<MovieDetails> = movieDetailsMutableLiveData

    private val showDetailsMutableLiveData: MutableLiveData<ShowDetails> = MutableLiveData()
    val showDetailsLiveData: LiveData<ShowDetails> = showDetailsMutableLiveData

    private val episodeDetailsMutableLiveData: MutableLiveData<List<VODService>> = MutableLiveData()
    val episodeDetailsLiveData: LiveData<List<VODService>> = episodeDetailsMutableLiveData

    fun getMovieDetails(id: String) {
        viewModelScope.launch {
            movieDetailsUseCase.invoke(id)?.let {
                movieDetailsMutableLiveData.value = it
            }
        }
    }

    fun getShowDetails(id: String) {
        viewModelScope.launch {
            showDetailsUseCase.invoke(id)?.let {
                showDetailsMutableLiveData.value = it
            }
        }
    }

    fun getEpisodeDetails(showId: String, seasonNumber: Int, episodeNo: Int) {
        viewModelScope.launch {
            episodeDetailsUseCase(showId, seasonNumber, episodeNo)?.let {
                episodeDetailsMutableLiveData.value = it.vodServices
            }
        }
    }

    fun getGenres(genres: List<String>): String {
        val sb = java.lang.StringBuilder()
        genres.forEachIndexed { index, genre ->
            sb.append(genre)
            if (index < genres.size - 1) {
                sb.append(", ")
            }
        }
        return sb.toString()
    }

    fun getPartners(partners: List<VODService>): String {
        val sb = java.lang.StringBuilder()
        partners.forEachIndexed { index, partner ->
            sb.append(partner.name)
            if (index < partners.size - 1) {
                sb.append(", ")
            }
        }
        return sb.toString()
    }
}