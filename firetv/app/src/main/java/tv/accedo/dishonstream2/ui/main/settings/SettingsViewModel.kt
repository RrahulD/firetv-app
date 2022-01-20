package tv.accedo.dishonstream2.ui.main.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.GetSettingsOptionsUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class SettingsViewModel(
    private val getSettingsOptionsUseCase: GetSettingsOptionsUseCase
) : BaseViewModel() {

    private val _optionsLiveData = MutableLiveData<List<String>>()
    val optionsLiveData: LiveData<List<String>> = _optionsLiveData
    var lastFocussedViewId: Int? = null

    init {
        getSettingOptions()
    }

    fun getSettingOptions() {
        viewModelScope.launch {
            try {
                onLoading(true)
                val settingOptions = getSettingsOptionsUseCase()
                _optionsLiveData.value = settingOptions
                onLoading(false)
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }
}