package tv.accedo.dishonstream2.ui.main.ondemand.seasondetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.EpisodeDetail
import tv.accedo.dishonstream2.domain.usecase.vod.GetSeasonDetailsCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class SeasonDetailsViewModel(
    private val seasonDetailsUseCase: GetSeasonDetailsCase
) : BaseViewModel() {
    private val episodeDetailsMutableLiveData: MutableLiveData<List<EpisodeDetail>> = MutableLiveData()
    val episodeDetailsLiveData: LiveData<List<EpisodeDetail>> = episodeDetailsMutableLiveData

    fun getSeasonDetails(showId: String, seasonNumber: Int) {
        viewModelScope.launch {
            onLoading(true)
            try {
                val episodeDetailList: List<EpisodeDetail> = seasonDetailsUseCase(showId, seasonNumber)
                onLoading(false)
                episodeDetailsMutableLiveData.value = episodeDetailList
            } catch (ex: Exception) {
                onLoading(false)
                Timber.e("Exception in SeasonDetailsViewModel")
                onError(ex)
            }
        }
    }
}