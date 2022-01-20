package tv.accedo.dishonstream2.ui.main.tvguide

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.TvGuideFragmentBinding
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.MainFragment
import tv.accedo.dishonstream2.ui.main.home.dialog.programinfo.ProgramInfoDialogFragment
import tv.accedo.dishonstream2.ui.main.search.ParentalPinChallenge
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsViewModel
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.classicepg.DataSource
import tv.accedo.dishonstream2.ui.main.tvguide.classicepg.FocusHandler
import tv.accedo.dishonstream2.ui.main.tvguide.classicepg.ModernDataSource
import tv.accedo.dishonstream2.ui.main.tvguide.player.PlayerActivity
import tv.accedo.dishonstream2.ui.main.tvguide.player.model.PipData
import tv.accedo.dishonstream2.ui.main.tvguide.view.PIPView


class TVGuideFragment : BaseFragment() {

    private lateinit var binding: TvGuideFragmentBinding
    private val tvGuideViewModel: TVGuideViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val parentalControlsViewModel: ParentalControlsViewModel by viewModel()
    private lateinit var playerActivityResultLauncher: ActivityResultLauncher<Intent>
    private var pipView: PIPView? = null
    private var pipData: PipData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = TvGuideFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result?.data
            if (result?.resultCode == Activity.RESULT_OK && data?.hasExtra("pipData") == true) {
                val pipData = data?.getParcelableExtra<PipData>("pipData")
                pipData?.let {
                    this.pipData = it
                    /* as we now we dont pip feature but will enable it for next phase
                    so commenting for now*/
                    //tvGuideViewModel.playContentInPip(it.channel, it.program)
                }
            }
        }

        tvGuideViewModel.epgLiveData.observe(viewLifecycleOwner) {
            when (tvGuideViewModel.styleEPG().value) {
                CLASSIC -> {
                    sharedAppViewModel.largeFontEnabledLiveData.value?.let { isZoomed ->
                        binding.epgView.show()
                        if (isZoomed) binding.epgView.attributes.rowHeight =
                            resources.getDimensionPixelSize(R.dimen.epg_row_height)
                        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val hour = currentTime.hour
                        val minute = if (currentTime.minute >= 30) 30 else 0

                        binding.epgView.attributes.setHourAndMinuteOffset(hour, minute)
                        binding.epgView.setDataSource(
                            DataSource(
                                it.toMutableList(),
                                binding.epgView,
                                timeFormat = sharedAppViewModel.timeFormatLiveData.value
                                    ?: TimeFormat.FORMAT_12_HOUR,
                                isZoomed,
                                ::onProgramClick
                            )
                        )

                        val focusHandler = object : FocusHandler(binding.epgView) {
                            override fun onFocusOutUp() {
                                val parent = parentFragment
                                if (parent is MainFragment) parent.requestFocusOnNavigation()
                            }
                        }
                        binding.epgView.setFocusHandler(focusHandler)

                        lifecycleScope.launch {
                            binding.epgView.requestFocus()
                            delay(100)
                            focusHandler.scrollByFocus(0, getFocusPosition(it), true)
                        }

                        tvGuideViewModel.scrollClassicEpgLiveData.removeObservers(viewLifecycleOwner)
                        tvGuideViewModel.scrollClassicEpgLiveData.observe(viewLifecycleOwner) { offset ->
                            focusHandler.scrollByFocus(offset.first, offset.second, true)
                        }
                    }
                }

                MODERN -> {
                    sharedAppViewModel.largeFontEnabledLiveData.value?.let { isZoomed ->
                        binding.modernEpgView.show()
                        binding.modernEpgView.setDataSource(
                            ModernDataSource(
                                it,
                                isZoomed,
                                binding.modernEpgView,
                                ::onProgramClick
                            )
                        )

                        lifecycleScope.launch {
                            binding.modernEpgView.requestFocus()
                            delay(100)
                            val position = getFocusPosition(it)
                            val lastChildPos = binding.modernEpgView.recyclerView.childCount - 1
                            val finalFocusPosition = if (position > lastChildPos) lastChildPos else position
                            binding.modernEpgView.recyclerView.scrollToPosition(position)
                            delay(100)
                            binding.modernEpgView.recyclerView.getChildAt(finalFocusPosition)?.requestFocus()
                        }
                    }
                }
            }
        }

        tvGuideViewModel.errorLiveData.observe(viewLifecycleOwner) {
            Firebase.crashlytics.run {
                log("Error while loading the TV Guide data")
                recordException(it)
            }
            binding.epgView.hide()
            binding.modernEpgView.hide()
            binding.progressBar.hide()
            binding.errorSection.show()
        }

        tvGuideViewModel.loadingLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        tvGuideViewModel.pipPlaybackInfo.observe(viewLifecycleOwner) {
            pipView = PIPView(requireContext(), it).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.BOTTOM or Gravity.END
                    val margin = requireContext().resources.getDimensionPixelSize(R.dimen._20dp)
                    setMargins(margin)
                }
            }
            binding.root.addView(pipView)
        }

        tvGuideViewModel.useLightTheme().observe(viewLifecycleOwner) {
            if (it) setTransparency()
        }
    }

    private fun getFocusPosition(channels: List<Channel>): Int {
        val channel = pipData?.channel ?: return 0
        val channelIndex = channels.indexOfFirst { it.serviceKey == channel.serviceKey }
        return if (channelIndex < 0) 0 else channelIndex
    }

    private fun onProgramClick(channel: Channel, program: Program) {
        val programInfoDialog = ProgramInfoDialogFragment.newInstance(channel, program)
        programInfoDialog.onWatchButtonClick = { c: Channel, p: Program, rating: String ->
            val activity = binding.root.context as Activity
            val pinChallenge = ParentalPinChallenge(rating,
                parentalControlsViewModel,
                activity as FragmentActivity
            ) { context, challengeCallback -> }.setOnPassedListener {
                startPlayerActivity(c, p)
                programInfoDialog.dismiss()
            }
            pinChallenge.run()
        }
        programInfoDialog.show(requireActivity().supportFragmentManager)
    }

    private fun startPlayerActivity(channel: Channel, program: Program) {
        pipView?.let { binding.root.removeView(it) }
        val playerIntent = PlayerActivity.getPlayerActivityIntent(requireContext(), channel, program)
        playerActivityResultLauncher.launch(playerIntent)
    }

    override fun onStart() {
        super.onStart()
        pipView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        tvGuideViewModel.getEpg()
        pipView?.onResume()
    }

    override fun onPause() {
        pipView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        pipView?.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        pipView?.onDestroy()
        playerActivityResultLauncher.unregister()
        super.onDestroyView()
    }

    private fun setTransparency() {
        binding.modernEpgView.setBackgroundColor(Color.TRANSPARENT)
        binding.epgView.setBackgroundColor(Color.TRANSPARENT)
        binding.progressBar.setBackgroundColor(Color.TRANSPARENT)
    }

    companion object {
        fun newInstance() = TVGuideFragment()
        const val CLASSIC = "Classic"
        const val MODERN = "Modern"
    }

}