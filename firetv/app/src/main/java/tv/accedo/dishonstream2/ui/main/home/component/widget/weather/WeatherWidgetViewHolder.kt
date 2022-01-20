package tv.accedo.dishonstream2.ui.main.home.component.widget.weather

import android.util.TypedValue
import android.view.View
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.WidgetWeatherLayoutBinding
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TemperatureFormat
import tv.accedo.dishonstream2.ui.base.resource.Resource
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.WeatherDialogFragment

@OptIn(KoinApiExtension::class)
class WeatherWidgetViewHolder(
    private val binding: WidgetWeatherLayoutBinding,
    private val fragmentManager: FragmentManager
    ) : WidgetViewHolder(binding.root), KoinComponent {

    private val weatherWidgetViewModel: WeatherWidgetViewModel by inject { parametersOf(getViewHolderScope()) }
    override val focusView: View = binding.container

    fun populate() {
        weatherWidgetViewModel.weatherDetails.observe(this) {
            when (it) {
                is Resource.Failure -> {
                }
                is Resource.Loading -> {
                }
                is Resource.Success -> populateWeatherDetails(it.data)
            }
        }
        weatherWidgetViewModel.loadWeatherDetails()
    }

    private fun populateWeatherDetails(weatherDetail: WeatherWidgetViewModel.WeatherDetails) {
        with(binding) {
            val weather = weatherDetail.weather
            place.text = weatherDetail.weather.place
            temperature.text = String.format(
                "%s%s",
                if (weatherDetail.temperatureFormat == TemperatureFormat.FAHRENHEIT)
                    weather.todayForeCast.avgTempF else weather.todayForeCast.avgTempC,
                weatherDetail.temperatureFormat.value
            )
            Glide.with(root)
                .load(weather.todayForeCast.iconUrl)
                .into(weatherIcon)

            container.setOnClickListener {
                WeatherDialogFragment.newInstance(weather, weatherDetail.temperatureFormat)
                    .show(fragmentManager)
            }
        }
    }

     fun setZoom(){
        binding.weatherIcon.layoutParams.width = binding.root.resources.getDimension(R.dimen._60dp).toInt()
        binding.weatherIcon.layoutParams.height = binding.root.resources.getDimension(R.dimen._60dp).toInt()
        binding.temperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        binding.place.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
    }
}