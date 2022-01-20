package tv.accedo.dishonstream2.ui.main.settings.appsettings.videoquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.GetVideoQualityUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.SetVideoQualityUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.VideoQuality
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class VideoQualityViewModel(
    private val setVideoQualityUseCase: SetVideoQualityUseCase,
    private val getVideoQualityUseCase: GetVideoQualityUseCase
) : BaseViewModel() {

    private val _videoQualityLiveData: MutableLiveData<VideoQuality> = MutableLiveData()
    val videoQualityLiveData: LiveData<VideoQuality> = _videoQualityLiveData

    init {
        getVideoQuality()
    }

    fun updateVideoQuality(quality: VideoQuality) {
        viewModelScope.launch {
            try {
                setVideoQualityUseCase(quality)
                getVideoQuality()
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    fun getVideoQuality() {
        viewModelScope.launch {
            try {
                val videoQuality = getVideoQualityUseCase()
                _videoQualityLiveData.value = videoQuality
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }
}