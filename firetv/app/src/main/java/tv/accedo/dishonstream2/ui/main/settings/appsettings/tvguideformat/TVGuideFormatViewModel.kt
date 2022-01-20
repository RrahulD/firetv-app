package tv.accedo.dishonstream2.ui.main.settings.appsettings.tvguideformat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.tvguide.GetTVGuideStyleUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.tvguide.SetTVGuideStyleUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class TVGuideFormatViewModel(
    private val getTVGuideStyleUseCase: GetTVGuideStyleUseCase,
    private val setTVGuideStyleUseCase: SetTVGuideStyleUseCase
) : BaseViewModel() {

    private val _tvGuideStyleLiveData: MutableLiveData<String> = MutableLiveData()
    val tvGuideStyleLiveData: LiveData<String> = _tvGuideStyleLiveData

    init {
        getTVGuideStyle()
    }

    fun getTVGuideStyle() {
        viewModelScope.launch {
            try {
                val tvGuideStyle = getTVGuideStyleUseCase()
                _tvGuideStyleLiveData.value = tvGuideStyle
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    fun saveTVGuideStyle(style: String) {
        viewModelScope.launch {
            try {
                setTVGuideStyleUseCase(style)
                getTVGuideStyle()
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }
}