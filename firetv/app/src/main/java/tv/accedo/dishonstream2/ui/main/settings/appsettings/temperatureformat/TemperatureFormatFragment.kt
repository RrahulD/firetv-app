package tv.accedo.dishonstream2.ui.main.settings.appsettings.temperatureformat

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.TemperatureFormatFragmentBinding
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TemperatureFormat
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class TemperatureFormatFragment : BaseSubSettingFragment() {

    companion object {
        fun newInstance() = TemperatureFormatFragment()
    }

    private val temperatureFormatViewModel: TemperatureFormatViewModel by viewModel()
    private lateinit var binding: TemperatureFormatFragmentBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = TemperatureFormatFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fahrenheit.setOnClickListener {
            temperatureFormatViewModel.saveTemperatureFormat(TemperatureFormat.FAHRENHEIT)
        }

        binding.celsius.setOnClickListener {
            temperatureFormatViewModel.saveTemperatureFormat(TemperatureFormat.CELSIUS)
        }

        temperatureFormatViewModel.temperatureFormatLiveData.observe(viewLifecycleOwner) {
            when (it.ordinal) {
                TemperatureFormat.FAHRENHEIT.ordinal -> {
                    binding.celsiusCheck.hide()
                    binding.fahrenheitCheck.show()
                    binding.fahrenheit.requestFocus()
                }

                TemperatureFormat.CELSIUS.ordinal -> {
                    binding.fahrenheitCheck.hide()
                    binding.celsiusCheck.show()
                    binding.celsius.requestFocus()
                }
            }
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            if (enabled) setZoom()
        }

        themeManager.getSettingsSubOptionsBackgroundDrawable()?.let {
            binding.celsius.background = it
            binding.fahrenheit.background = it.copy()
        }
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.celciusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.fahrenheitText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.celsiusCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.celsiusCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()

        binding.fahrenheitCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.fahrenheitCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()
    }
}