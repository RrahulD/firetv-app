package tv.accedo.dishonstream2.ui.main.tvguide.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bitmovin.player.api.deficiency.SourceErrorCode
import com.google.android.exoplayer2.source.BehindLiveWindowException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ActivityPlayerBinding
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.ui.base.BaseActivity
import tv.accedo.dishonstream2.ui.main.home.dialog.ContentErrorDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.player.model.PipData
import tv.accedo.dishonstream2.ui.main.tvguide.player.model.PlaybackInfo
import tv.accedo.dishonstream2.ui.main.tvguide.player.view.DishPlayerView
import tv.accedo.dishonstream2.ui.main.tvguide.player.view.miniepg.MiniEPGView

class PlayerActivity : BaseActivity() {

    private val playerViewModel: PlayerViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by viewModel()
    private var dishPlayerView: DishPlayerView? = null
    private var miniEpgView: MiniEPGView? = null
    private var autoHidePlayerControlsJob: Job? = null
    private var isZoomEnabled = false
    private var currentPlayingChannel: Channel? = null
    private var mLastKeyDownTime: Long = 0

    companion object {
        private const val KEY_CHANNEL = "channel"
        private const val KEY_PROGRAM = "program"

        fun getPlayerActivityIntent(context: Context, channel: Channel, program: Program): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(KEY_CHANNEL, channel)
            intent.putExtra(KEY_PROGRAM, program)
            return intent
        }
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(FLAG_KEEP_SCREEN_ON)
        initViewModelObservers()
    }

    private fun tryPlayingContent(channel: Channel?) {
        if (channel != null) {
            playChannel(channel)
        } else {
            showPlayerErrorDialog()
        }
    }

    private fun initViewModelObservers() {
        playerViewModel.largeFontEnabledLiveData.observe(this) { enabled ->
            isZoomEnabled = enabled
            tryPlayingContent(intent.getParcelableExtra(KEY_CHANNEL))
        }

        playerViewModel.playbackInfo.observe(this) { playbackInfo ->
            if (playbackInfo == null) return@observe
            addPlayer(createDishPlayer(playbackInfo))
        }

        playerViewModel.programDetail.observe(this) { programDetail ->
            setProgramDetails(programDetail)
        }

        playerViewModel.miniEpgLiveData.observe(this) { (showMiniEpg, channels) ->
            if (showMiniEpg) {
                miniEpgView?.let { binding.dishPlayerContainer.removeView(it) }
                dishPlayerView?.hidePlayerControls()
                miniEpgView = MiniEPGView(this, channels) { channel, _ ->
                    playerViewModel.hideMiniEpg()
                    if (channel.serviceKey != currentPlayingChannel?.serviceKey)
                        lifecycleScope.launch {
                            delay(500)
                            playChannel(channel)
                        }
                }
                miniEpgView?.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                binding.dishPlayerContainer.addView(miniEpgView)
                miniEpgView?.requestFocus()
            } else {
                miniEpgView?.let { binding.dishPlayerContainer.removeView(it) }
            }
        }

        playerViewModel.loadingLiveData.observe(this) {
            binding.progressBar.isVisible = it
        }

        playerViewModel.errorLiveData.observe(this) { showPlayerErrorDialog() }
    }

    private fun addPlayer(player: DishPlayerView) {
        dishPlayerView?.let { binding.dishPlayerContainer.removeView(it) }
        dishPlayerView = player
        binding.dishPlayerContainer.addView(dishPlayerView)
    }

    private fun restartPlayerWithLowerCappedBitRate(playbackInfo: PlaybackInfo) {
        val audioTrackId = dishPlayerView?.getAudioTrackId()
        val subTitleTrackId = dishPlayerView?.getSubtitleTrackId()
        addPlayer(createDishPlayer(playbackInfo, true, audioTrackId, subTitleTrackId))
        setProgramDetails(playerViewModel.programDetail.value)
    }

    private fun createDishPlayer(
        playbackInfo: PlaybackInfo,
        capToLowBitRateStream: Boolean = false,
        audioTrackId: String? = null,
        subTitleTrackId: String? = null
    ) = DishPlayerView(
        this, playbackInfo, supportFragmentManager, isZoomEnabled, capToLowBitRateStream, audioTrackId, subTitleTrackId
    ).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        onMiniEpgButtonClick = { playerViewModel.showMiniEpg() }
        onBackButtonClick = { super.onBackPressed() }
        onError = { error ->
            if (error.data is BehindLiveWindowException) {
                restartPlayerWithLowerCappedBitRate(playbackInfo)
                true
            } else if (error.code == SourceErrorCode.DrmGeneral) {
                // in case of Generic DRM error (2301), restart the playback on the current playing channel.
                val channel = playerViewModel.programDetail.value?.channel
                if (channel != null) {
                    playChannel(channel)
                    true
                } else false
            } else {
                setResult(RESULT_CANCELED)
                false
            }
        }
    }

    private fun setProgramDetails(programDetail: PlayerViewModel.ProgramDetails?) {
        val program = programDetail?.program ?: return
        val channel = programDetail.channel
        val description = programDetail.programInfo?.description ?: ""
        setResult(RESULT_OK, intent.apply { putExtra("pipData", PipData(channel, program)) })
        currentPlayingChannel = channel

        dishPlayerView?.setProgramDetails(
            program.name,
            program.startTime,
            program.endTime,
            description,
            channel.detail?.logoURL,
            programDetail.timeFormat
        )
    }

    private fun showPlayerErrorDialog() {
        ContentErrorDialogFragment
            .newInstance(getString(R.string.content_unavailable), getString(R.string.content_unavailable_message))
            .apply { onActionButtonClick = { finish() } }
            .show(supportFragmentManager)
    }

    private fun playChannel(channel: Channel) {
        with(binding) {
            dishPlayerView?.let { dishPlayerContainer.removeView(it) }
            miniEpgView?.let { dishPlayerContainer.removeView(it) }
            dishPlayerView = null
            miniEpgView = null
        }
        playerViewModel.playContent(channel)
    }

    override fun onStart() {
        dishPlayerView?.onStart()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        dishPlayerView?.onResume()
    }

    override fun onPause() {
        dishPlayerView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        dishPlayerView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        dishPlayerView?.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (dishPlayerView?.isPlayerControlsShown() == true) {
            cancelAutoHidePlayerControlsJob()
            dishPlayerView?.hidePlayerControls()
        } else if (playerViewModel.isMiniEpgVisible()) {
            playerViewModel.hideMiniEpg()
        } else
            super.onBackPressed()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val current = System.currentTimeMillis()
        return if (current - mLastKeyDownTime < 300) {
            true
        } else {
            mLastKeyDownTime = current
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (!playerViewModel.isMiniEpgVisible())
            when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                KeyEvent.KEYCODE_ENTER,
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    dishPlayerView?.showPlayerControls()
                    scheduleAutoHidePlayerControlsJob()
                }
            }
        return super.onKeyUp(keyCode, event)
    }

    private fun scheduleAutoHidePlayerControlsJob() {
        cancelAutoHidePlayerControlsJob()
        autoHidePlayerControlsJob = lifecycleScope.launch {
            delay(5000)
            dishPlayerView?.hidePlayerControls()
        }
    }

    private fun cancelAutoHidePlayerControlsJob() {
        if (autoHidePlayerControlsJob?.isActive == true)
            autoHidePlayerControlsJob?.cancel()
    }
}