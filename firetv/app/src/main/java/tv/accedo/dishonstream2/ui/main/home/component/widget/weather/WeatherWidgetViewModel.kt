package tv.accedo.dishonstream2.ui.main.home.component.widget.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.home.weather.Weather
import tv.accedo.dishonstream2.domain.usecase.home.GetWeatherDetailUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTemperatureFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TemperatureFormat
import tv.accedo.dishonstream2.ui.base.resource.Resource
import tv.accedo.dishonstream2.ui.base.viewholder.BaseViewHolderViewModel

class WeatherWidgetViewModel(
    private val getWeatherDetailUseCase: GetWeatherDetailUseCase,
    private val getTemperatureFormatUseCase: GetTemperatureFormatUseCase,
    override val viewHolderScope: CoroutineScope
) : BaseViewHolderViewModel {

    data class WeatherDetails(
        val weather: Weather,
        val temperatureFormat: TemperatureFormat
    )

    private val _weatherDetails = MutableLiveData<Resource<WeatherDetails>>()
    val weatherDetails: LiveData<Resource<WeatherDetails>> = _weatherDetails

    fun loadWeatherDetails() {
        viewHolderScope.launch {
            _weatherDetails.value = Resource.Loading()
            try {
                _weatherDetails.value = Resource.Success(
                    WeatherDetails(getWeatherDetailUseCase(), getTemperatureFormatUseCase())
                )
            } catch (ex: Exception) {
                Timber.e("Error while loading the weather details in the widget")
                _weatherDetails.value = Resource.Failure(ex)
            }
        }
    }
}