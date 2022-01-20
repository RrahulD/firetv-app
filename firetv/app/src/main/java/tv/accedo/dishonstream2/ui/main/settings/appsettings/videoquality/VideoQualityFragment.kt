package tv.accedo.dishonstream2.ui.main.settings.appsettings.videoquality

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.VideoQualityFragmentBinding
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.VideoQuality
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class VideoQualityFragment : BaseSubSettingFragment() {

    private lateinit var binding: VideoQualityFragmentBinding
    private val videoQualityViewModel: VideoQualityViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = VideoQualityFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.auto.setOnClickListener { videoQualityViewModel.updateVideoQuality(VideoQuality.AUTO) }

        binding.high.setOnClickListener { videoQualityViewModel.updateVideoQuality(VideoQuality.HIGH) }

        binding.medium.setOnClickListener { videoQualityViewModel.updateVideoQuality(VideoQuality.MEDIUM) }

        binding.low.setOnClickListener { videoQualityViewModel.updateVideoQuality(VideoQuality.LOW) }

        videoQualityViewModel.videoQualityLiveData.observe(viewLifecycleOwner) { quality ->
            clearAllSelection()
            when (quality.ordinal) {
                VideoQuality.AUTO.ordinal -> {
                    binding.autoCheck.show()
                    binding.auto.requestFocus()
                }

                VideoQuality.HIGH.ordinal -> {
                    binding.highCheck.show()
                    binding.high.requestFocus()
                }

                VideoQuality.MEDIUM.ordinal -> {
                    binding.mediumCheck.show()
                    binding.medium.requestFocus()
                }

                VideoQuality.LOW.ordinal -> {
                    binding.lowCheck.show()
                    binding.low.requestFocus()
                }
            }
        }
        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            if (enabled) setZoom()
        }
    }

    private fun clearAllSelection() {
        with(binding) {
            autoCheck.hide()
            highCheck.hide()
            mediumCheck.hide()
            lowCheck.hide()
        }
    }

    companion object {
        fun newInstance() = VideoQualityFragment()
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.autoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.highText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.mediumText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.lowText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.autoCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.autoCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()

        binding.highCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.highCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()

        binding.mediumCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.mediumCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()

        binding.lowCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.lowCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()
    }

}