package tv.accedo.dishonstream2.ui.main.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTimeFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.SetTimeFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.domain.usecase.settings.theme.EnableLargeFontSizeUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.theme.IsLargeFontSizeEnabledUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class SharedAppViewModel(
    private val isLargeFontSizeEnabledUseCase: IsLargeFontSizeEnabledUseCase,
    private val enableLargeFontSizeUseCase: EnableLargeFontSizeUseCase,
    private val getTimeFormatUseCase: GetTimeFormatUseCase,
    private val setTimeFormatUseCase: SetTimeFormatUseCase
) : BaseViewModel() {

    private val _largeFontEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val largeFontEnabledLiveData: LiveData<Boolean> = _largeFontEnabledLiveData

    private val _timeFormatLiveData: MutableLiveData<TimeFormat> = MutableLiveData()
    val timeFormatLiveData: LiveData<TimeFormat> = _timeFormatLiveData

    init {
        getIsLargeFontSizeEnabled()
        getTimeFormat()
    }

    private fun getIsLargeFontSizeEnabled() {
        viewModelScope.launch {
            try {
                val isLargeFontEnabled = isLargeFontSizeEnabledUseCase()
                _largeFontEnabledLiveData.value = isLargeFontEnabled
            } catch (ex: Exception) {
                _largeFontEnabledLiveData.value = false
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

    fun saveTimeFormat(format: TimeFormat) {
        viewModelScope.launch {
            try {
                setTimeFormatUseCase(format)
                getTimeFormat()
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    fun enableLargeFont(enabled: Boolean) {
        viewModelScope.launch {
            try {
                enableLargeFontSizeUseCase(enabled)
                getIsLargeFontSizeEnabled()
            } catch (ex: Exception) {
            }
        }
    }
}