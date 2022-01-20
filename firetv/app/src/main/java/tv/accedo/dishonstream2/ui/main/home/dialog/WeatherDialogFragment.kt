package tv.accedo.dishonstream2.ui.main.home.dialog

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewWeatherDialogBinding
import tv.accedo.dishonstream2.domain.model.home.weather.Weather
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TemperatureFormat
import tv.accedo.dishonstream2.extensions.capitalise
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class WeatherDialogFragment : BaseFullScreenDialogFragment() {

    companion object {
        private const val KEY_WEATHER_INFO = "weather_info"
        private const val KEY_TEMPERATURE_FORMAT = "temperature_format"

        fun newInstance(weather: Weather, temperatureFormat: TemperatureFormat): WeatherDialogFragment =
            WeatherDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_WEATHER_INFO, weather)
                    putInt(KEY_TEMPERATURE_FORMAT, temperatureFormat.ordinal)
                }
            }
    }

    private lateinit var binding: ViewWeatherDialogBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val themeManager: ThemeManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ViewWeatherDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            populateData()
            if (enabled) setZoomed(enabled)
        })
    }

    private fun setZoomed(enabled: Boolean) {
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = if (enabled) 0.5f else 0.4f
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight = if (enabled) 0.7f else 0.6f

        binding.todayForecastIcon.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._65dp else R.dimen._60dp).toInt()
        binding.todayForecastIcon.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._65dp else R.dimen._60dp).toInt()

        binding.city.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 25f else 20f)
        binding.weatherDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 19f else 14f)

        binding.nowLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)
        binding.highLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)
        binding.lowLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)

        binding.temperatureNow.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 23f else 18f)
        binding.temperatureHigh.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 23f else 18f)
        binding.temperatureLow.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 23f else 18f)


        binding.todayPlus1Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)
        binding.todayPlus2Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)
        binding.todayPlus3Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)
        binding.todayPlus4Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)
        binding.todayPlus5Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)

        binding.todayPlus1ForecastIcon.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus1ForecastIcon.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus2ForecastIcon.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus2ForecastIcon.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus3ForecastIcon.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus3ForecastIcon.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus4ForecastIcon.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus4ForecastIcon.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus5ForecastIcon.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()
        binding.todayPlus5ForecastIcon.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._30dp).toInt()


        (binding.btnOkay.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight =
            if (enabled) 0.5f else 0.4f
        binding.btnOkay.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 17f else 12f)
    }


    private fun populateData() {
        val weather = arguments?.getParcelable<Weather>(KEY_WEATHER_INFO)
        val temperatureFormat = TemperatureFormat.values().find {
            it.ordinal == arguments?.getInt(KEY_TEMPERATURE_FORMAT)
        }

        themeManager.getPrimaryButtonBackgroundDrawable()?.let { binding.btnOkay.background = it }

        if (weather != null && temperatureFormat != null) {
            with(binding) {
                city.text = weather.place.capitalise()

                // today forecast
                with(weather.todayForeCast) {
                    weatherDescription.text = description
                    Glide.with(root)
                        .load(weather.todayForeCast.iconUrl)
                        .into(todayForecastIcon)

                    if (temperatureFormat == TemperatureFormat.FAHRENHEIT) {
                        temperatureNow.text = String.format("%d°", avgTempF.toInt())
                        temperatureLow.text = String.format("%d°", minTempF.toInt())
                        temperatureHigh.text = String.format("%d°", maxTempF.toInt())
                    } else {
                        temperatureNow.text = String.format("%d°", avgTempC.toInt())
                        temperatureLow.text = String.format("%d°", minTempC.toInt())
                        temperatureHigh.text = String.format("%d°", maxTempC.toInt())
                    }
                }

                // today + 1 forecast
                weather.futureForecast.getOrNull(0)?.let {
                    todayPlus1Label.text = it.day
                    Glide.with(root)
                        .load(it.iconUrl)
                        .into(todayPlus1ForecastIcon)
                }

                // today + 2 forecast
                weather.futureForecast.getOrNull(1)?.let {
                    todayPlus2Label.text = it.day
                    Glide.with(root)
                        .load(it.iconUrl)
                        .into(todayPlus2ForecastIcon)
                }

                // today + 3 forecast
                weather.futureForecast.getOrNull(2)?.let {
                    todayPlus3Label.text = it.day
                    Glide.with(root)
                        .load(it.iconUrl)
                        .into(todayPlus3ForecastIcon)
                }

                // today + 4 forecast
                weather.futureForecast.getOrNull(3)?.let {
                    todayPlus4Label.text = it.day
                    Glide.with(root)
                        .load(it.iconUrl)
                        .into(todayPlus4ForecastIcon)
                }

                // today + 5 forecast
                weather.futureForecast.getOrNull(4)?.let {
                    todayPlus5Label.text = it.day
                    Glide.with(root)
                        .load(it.iconUrl)
                        .into(todayPlus5ForecastIcon)
                }

                btnOkay.setOnClickListener { dismiss() }
            }
        }
    }
}