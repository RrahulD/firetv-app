package tv.accedo.dishonstream2.ui.main.settings.appsettings.tvguideformat

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.TvGuideFormatFragmentBinding
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class TVGuideFormatFragment : BaseSubSettingFragment() {

    companion object {
        const val CLASSIC = "Classic"
        const val MODERN = "Modern"

        fun newInstance() = TVGuideFormatFragment()
    }

    private lateinit var binding: TvGuideFormatFragmentBinding
    private val tvGuideFormatViewModel: TVGuideFormatViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TvGuideFormatFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.classic.setOnClickListener {
            tvGuideFormatViewModel.saveTVGuideStyle(CLASSIC)
        }

        binding.modern.setOnClickListener {
            tvGuideFormatViewModel.saveTVGuideStyle(MODERN)
        }

        tvGuideFormatViewModel.tvGuideStyleLiveData.observe(viewLifecycleOwner) { tvGuideStyle ->
            when (tvGuideStyle) {
                CLASSIC -> {
                    binding.classicCheck.show()
                    binding.modernCheck.hide()
                    binding.classic.requestFocus()
                }

                MODERN -> {
                    binding.modernCheck.show()
                    binding.classicCheck.hide()
                    binding.modern.requestFocus()
                }
            }
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            if (enabled) setZoom()
        }

        themeManager.getSettingsSubOptionsBackgroundDrawable()?.let {
            binding.classic.background = it
            binding.modern.background = it.copy()
        }
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.classicText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.modernText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.classicCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.classicCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()

        binding.modernCheck.layoutParams.height =
            resources.getDimension(R.dimen._28dp).toInt()
        binding.modernCheck.layoutParams.width =
            resources.getDimension(R.dimen._28dp).toInt()
    }
}