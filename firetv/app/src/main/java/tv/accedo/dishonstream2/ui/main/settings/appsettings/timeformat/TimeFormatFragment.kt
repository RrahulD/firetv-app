package tv.accedo.dishonstream2.ui.main.settings.appsettings.timeformat


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.TimeFormatFragmentBinding
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class TimeFormatFragment : BaseSubSettingFragment() {

    companion object {
        fun newInstance() = TimeFormatFragment()
    }

    private lateinit var binding: TimeFormatFragmentBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = TimeFormatFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.twelveHour.setOnClickListener {
            sharedAppViewModel.saveTimeFormat(TimeFormat.FORMAT_12_HOUR)
        }

        binding.twentyFourHour.setOnClickListener {
            sharedAppViewModel.saveTimeFormat(TimeFormat.FORMAT_24_HOUR)
        }

        sharedAppViewModel.timeFormatLiveData.observe(viewLifecycleOwner) { timeFormat ->
            when (timeFormat.ordinal) {
                TimeFormat.FORMAT_12_HOUR.ordinal -> {
                    binding.twentyFourCheck.hide()
                    binding.twelveCheck.show()
                    binding.twelveHour.requestFocus()
                }
                TimeFormat.FORMAT_24_HOUR.ordinal -> {
                    binding.twelveCheck.hide()
                    binding.twentyFourCheck.show()
                    binding.twentyFourHour.requestFocus()
                }
            }
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            if (enabled) setZoom()
        }

        themeManager.getSettingsSubOptionsBackgroundDrawable()?.let {
            binding.twelveHour.background = it
            binding.twentyFourHour.background = it.copy()
        }
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.twelveHrText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.twentyFourHrText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.twelveCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.twelveCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()

        binding.twentyFourCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.twentyFourCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()
    }
}