package tv.accedo.dishonstream2.ui.main.settings.appsettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.GetAppSettingsOptionsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTemperatureFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTimeFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TemperatureFormat
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.GetVideoQualityUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.IsAutoPlayNextEpisodeEnabledUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.SetAutoPlayNextEpisodeUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.VideoQuality
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.tvguide.GetTVGuideStyleUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class AppSettingsViewModel(
    private val getTVGuideStyleUseCase: GetTVGuideStyleUseCase,
    private val getVideoQualityUseCase: GetVideoQualityUseCase,
    private val isAutoPlayNextEpisodeEnabledUseCase: IsAutoPlayNextEpisodeEnabledUseCase,
    private val setAutoPlayNextEpisodeUseCase: SetAutoPlayNextEpisodeUseCase,
    private val getTimeFormatUseCase: GetTimeFormatUseCase,
    private val getTemperatureFormatUseCase: GetTemperatureFormatUseCase,
    private val getAppSettingsOptionsUseCase: GetAppSettingsOptionsUseCase,
) : BaseViewModel() {

    private val _tvGuideStyleLiveData: MutableLiveData<String> = MutableLiveData()
    val tvGuideStyleLiveData: LiveData<String> = _tvGuideStyleLiveData

    private val _videoQualityLiveData: MutableLiveData<VideoQuality> = MutableLiveData()
    val videoQualityLiveData: LiveData<VideoQuality> = _videoQualityLiveData

    private val _autoPlayNextEpisodeLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val autoPlayNextEpisodeLiveData: LiveData<Boolean> = _autoPlayNextEpisodeLiveData

    private val _appSettingOptionsLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val appSettingOptionsLiveData: LiveData<List<String>> = _appSettingOptionsLiveData

    private val _timeFormatLiveData: MutableLiveData<TimeFormat> = MutableLiveData()
    val timeFormatLiveData: LiveData<TimeFormat> = _timeFormatLiveData

    private val _temperatureFormatLiveData: MutableLiveData<TemperatureFormat> = MutableLiveData()
    val temperatureFormatLiveData: MutableLiveData<TemperatureFormat> = _temperatureFormatLiveData

    init {
        getAppSettingOptions()
        getTVGuideStyle()
        getVideoQuality()
        getIsAutoPlayNextEpisode()
        getTimeFormat()
        getTemperatureFormat()
    }

    private fun getAppSettingOptions() {
        viewModelScope.launch {
            try {
                val options = getAppSettingsOptionsUseCase()
                _appSettingOptionsLiveData.value = options
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    private fun getTVGuideStyle() {
        viewModelScope.launch {
            try {
                val tvGuideStyle = getTVGuideStyleUseCase()
                _tvGuideStyleLiveData.value = tvGuideStyle
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    private fun getVideoQuality() {
        viewModelScope.launch {
            try {
                val videoQuality = getVideoQualityUseCase()
                _videoQualityLiveData.value = videoQuality
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    private fun getIsAutoPlayNextEpisode() {
        viewModelScope.launch {
            try {
                val shouldAutoPlayNextEpisode = isAutoPlayNextEpisodeEnabledUseCase()
                _autoPlayNextEpisodeLiveData.value = shouldAutoPlayNextEpisode
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    private fun getTimeFormat() {
        viewModelScope.launch {
            try {
                val timeFormat = getTimeFormatUseCase()
                _timeFormatLiveData.value = timeFormat
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    private fun getTemperatureFormat() {
        viewModelScope.launch {
            try {
                val temperatureFormat = getTemperatureFormatUseCase()
                _temperatureFormatLiveData.value = temperatureFormat
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    fun updateAutoPlayNextEpisode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                setAutoPlayNextEpisodeUseCase(enabled)
                getIsAutoPlayNextEpisode()
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }
}