package tv.accedo.dishonstream2.ui.main.settings.appsettings

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.databinding.AppSettingsFragmentBinding
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.GetAppSettingsOptionsUseCase
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.ui.main.settings.SettingsFragment
import tv.accedo.dishonstream2.ui.main.settings.appsettings.temperatureformat.TemperatureFormatFragment
import tv.accedo.dishonstream2.ui.main.settings.appsettings.timeformat.TimeFormatFragment
import tv.accedo.dishonstream2.ui.main.settings.appsettings.tvguideformat.TVGuideFormatFragment
import tv.accedo.dishonstream2.ui.main.settings.appsettings.videoquality.VideoQualityFragment
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class AppSettingsFragment : BaseSubSettingFragment() {

    private lateinit var binding: AppSettingsFragmentBinding
    private val appSettingViewModel: AppSettingsViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AppSettingsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.videoQuality.setOnClickListener {
            setLastFocussedViewId(it.id)
            replaceFragment(VideoQualityFragment.newInstance())
        }
        binding.autoPlayNextEpisode.setOnClickListener {
            setLastFocussedViewId(it.id)
            binding.autoplayValue.isChecked = !binding.autoplayValue.isChecked
            binding.autoplayValue.text = if (binding.autoplayValue.isChecked) "ON" else "OFF"
            appSettingViewModel.updateAutoPlayNextEpisode(binding.autoplayValue.isChecked)
        }

        binding.tvGuideFormat.setOnClickListener {
            setLastFocussedViewId(it.id)
            replaceFragment(TVGuideFormatFragment.newInstance())
        }

        binding.enableLargeFontSize.setOnClickListener {
            sharedAppViewModel.enableLargeFont(binding.enableLargeFontSizeValue.text == "OFF")
        }

        binding.timeFormat.setOnClickListener {
            setLastFocussedViewId(it.id)
            replaceFragment(TimeFormatFragment.newInstance())
        }
        binding.temperatureFormat.setOnClickListener {
            setLastFocussedViewId(it.id)
            val parent = (parentFragment as SettingsFragment)
            parent.replaceFragment(TemperatureFormatFragment.newInstance())
        }

        appSettingViewModel.appSettingOptionsLiveData.observe(viewLifecycleOwner) { options ->
            for (option in options) {
                // No requirement as of now so hiding playback section.
                /* if (option == GetAppSettingsOptionsUseCase.PLAYBACK_KEY) {
                     binding.playbackSection.visibility = View.VISIBLE
                 }*/

                if (option == GetAppSettingsOptionsUseCase.TV_GUIDE_KEY) {
                    binding.tvGuideSection.visibility = View.VISIBLE
                }

                if (option == GetAppSettingsOptionsUseCase.FORMAT_OPTIONS_KEY) {
                    binding.formatOptionsSection.visibility = View.VISIBLE
                }
            }
        }

        appSettingViewModel.tvGuideStyleLiveData.observe(viewLifecycleOwner, { tvGuideStyle ->
            binding.tvGuideFormatValue.text = tvGuideStyle.uppercase()
        })

        appSettingViewModel.videoQualityLiveData.observe(viewLifecycleOwner, { videoQuality ->
            binding.videoQualityValue.text = videoQuality.value.uppercase()
        })

        appSettingViewModel.autoPlayNextEpisodeLiveData.observe(viewLifecycleOwner, { autoPlayNextEpisode ->
            binding.autoplayValue.isChecked = autoPlayNextEpisode
            binding.autoplayValue.text = if (autoPlayNextEpisode) "ON" else "OFF"
        })

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            binding.enableLargeFontSizeValue.text = if (enabled) "ON" else "OFF"
            setZoom(enabled)
        })

        appSettingViewModel.timeFormatLiveData.observe(viewLifecycleOwner, { timeFormat ->
            binding.timeFormatValue.text = timeFormat.value
        })

        appSettingViewModel.temperatureFormatLiveData.observe(viewLifecycleOwner, { temperatureFormat ->
            binding.temperatureFormatValue.text = temperatureFormat.value
        })

        themeManager.getSettingsSubOptionsBackgroundDrawable()?.let {
            binding.tvGuideFormat.background = it
            binding.enableLargeFontSize.background = it.copy()
            binding.timeFormat.background = it.copy()
            binding.temperatureFormat.background = it.copy()
        }
    }

    private fun setZoom(enabled: Boolean) {
        binding.tvGuideTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        binding.tvGuideFormatTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.tvGuideFormatValue.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.timeFormatValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        binding.timeFormatTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        binding.temperatureFormatValue.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.temperatureFormatTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.enableLargeFontSizeValue.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.enableLargeFontSizeTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.formatOptionsTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.playbackTitleText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.videoQualityText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        binding.videoQualityValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        binding.autoPlayNexEpisodeText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 18f else 14f
        )
        binding.autoplayValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
    }

    companion object {
        fun newInstance() = AppSettingsFragment()
    }


}