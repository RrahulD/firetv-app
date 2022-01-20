package tv.accedo.dishonstream2.ui.main.settings.appsettings.temperatureformat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTemperatureFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.SetTemperatureFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TemperatureFormat
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class TemperatureFormatViewModel(
    private val getTemperatureFormatUseCase: GetTemperatureFormatUseCase,
    private val setTemperatureFormatUseCase: SetTemperatureFormatUseCase,
) : BaseViewModel() {

    private val _temperatureFormatLiveData: MutableLiveData<TemperatureFormat> = MutableLiveData()
    val temperatureFormatLiveData: LiveData<TemperatureFormat> = _temperatureFormatLiveData

    init {
        getTemperatureFormat()
    }

    fun getTemperatureFormat() {
        viewModelScope.launch {
            try {
                val temperatureFormat = getTemperatureFormatUseCase()
                _temperatureFormatLiveData.value = temperatureFormat
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    fun saveTemperatureFormat(format: TemperatureFormat) {
        viewModelScope.launch {
            try {
                setTemperatureFormatUseCase(format)
                getTemperatureFormat()
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }
}