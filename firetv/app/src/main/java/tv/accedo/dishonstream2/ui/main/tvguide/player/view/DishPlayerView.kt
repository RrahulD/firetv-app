package tv.accedo.dishonstream2.ui.main.tvguide.player.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.buffer.BufferConfig
import com.bitmovin.player.api.buffer.BufferMediaTypeConfig
import com.bitmovin.player.api.deficiency.ErrorEvent
import com.bitmovin.player.api.drm.WidevineConfig
import com.bitmovin.player.api.event.Event
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.media.AdaptationConfig
import com.bitmovin.player.api.media.audio.AudioTrack
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceType
import com.bumptech.glide.Glide
import kotlinx.datetime.Instant
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewDishPlayerBinding
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.extensions.getTimeString
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.tvguide.player.dialog.CaptionsAndDescriptiveAudioDialogFragment
import tv.accedo.dishonstream2.ui.main.tvguide.player.model.PlaybackInfo
import tv.accedo.dishonstream2.ui.theme.ThemeManager
import java.util.*

@SuppressLint("ViewConstructor")
@OptIn(KoinApiExtension::class)
class DishPlayerView constructor(
    context: Context,
    private var playbackInfo: PlaybackInfo,
    private val fragmentManager: FragmentManager,
    private val isZoomEnabled: Boolean,
    private val capToLowBitRateStream: Boolean = false,
    private val audioTrackId: String? = null,
    private val subtitleTrackId: String? = null
) : FrameLayout(context), KoinComponent {

    companion object {
        private const val LOW_BITRATE_STREAM_CAP = 256000
    }

    private val themeManager: ThemeManager by inject()

    private val binding: ViewDishPlayerBinding =
        ViewDishPlayerBinding.inflate(LayoutInflater.from(context), this, true)

    private val player = Player.create(
        context,
        PlayerConfig(key = playbackInfo.playerLicense).apply {
            bufferConfig = BufferConfig(BufferMediaTypeConfig(6.0))
            playbackConfig.isTunneledPlaybackEnabled = true
            if (capToLowBitRateStream)
                adaptationConfig = AdaptationConfig(maxSelectableVideoBitrate = LOW_BITRATE_STREAM_CAP)
        }
    ).apply {
        setSurface(binding.surfaceView.holder)
        binding.surfaceView.setZOrderMediaOverlay(false)
        binding.surfaceView.setZOrderOnTop(false)
    }

    private val playerView = PlayerView(context, player).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private var playerControlShown = false

    // listeners
    var onMiniEpgButtonClick: (() -> Unit)? = null
    var onBackButtonClick: (() -> Unit)? = null
    var onError: ((ErrorEvent) -> Boolean)? = null

    init {
        themeManager.getPlayerControlBackgroundDrawable()?.let {
            with(binding) {
                btnBack.background = it
                btnMiniEpg.background = it.copy()
                btnCaptions.background = it.copy()
                btnPlayPause.background = it.copy()
                btnRecord.background = it.copy()
                btnRestart.background = it.copy()
            }
        }
    }

    private val seekBarChangeListener: SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Only seek/timeShift when the user changes the progress (and not the TimeChangedEvent)
                if (fromUser) {
                    // If the current stream is a live stream, we have to use the timeShift method
                    if (!player.isLive) {
                        player.seek(progress / 1000.0)
                    } else {
                        player.timeShift((progress - seekBar.max) / 1000.0)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addListeners()
        if (isZoomEnabled) setZoom()
        val sourceConfig = SourceConfig(playbackInfo.dashURL, SourceType.Dash)
        sourceConfig.drmConfig = WidevineConfig(playbackInfo.drmLicenseURL).apply {
            httpHeaders = mutableMapOf("PreAuthorization" to playbackInfo.drmToken)
        }
        player.load(sourceConfig)
        showPlayerControls()
    }

    override fun onDetachedFromWindow() {
        player.off(::updateUI)
        player.destroy()
        super.onDetachedFromWindow()
    }

    private fun updateUI(event: Event) {
        when (event) {
            is PlayerEvent.TimeChanged -> updateProgress()

            is PlayerEvent.Playing -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                binding.progressBar.hide()
            }

            is PlayerEvent.Paused -> binding.btnPlayPause.setImageResource(R.drawable.ic_play)

            is PlayerEvent.StallStarted -> binding.progressBar.show()

            is PlayerEvent.StallEnded -> binding.progressBar.hide()

            is PlayerEvent.RenderFirstFrame -> {
                hidePlayerControls()
                setAudioAndSubtitleTracks(audioTrackId, subtitleTrackId)
            }

            is PlayerEvent.Error -> onPlayerError(event)

            is PlayerEvent.CueEnter -> {
                binding.subtitles.text = event.text
                binding.subtitles.show()
            }

            is PlayerEvent.CueExit -> binding.subtitles.hide()

            is SourceEvent.Load -> binding.progressBar.show()

            is SourceEvent.Loaded -> {
                binding.progressBar.hide()
                player.play()
            }

            is SourceEvent.Error -> onPlayerError(event)

            else -> {
            }
        }
    }

    private fun onPlayerError(errorEvent: ErrorEvent) {
        if (onError?.invoke(errorEvent) == true) return
        binding.progressBar.hide()
        binding.errorMessage.show()
        binding.errorMessage.text = context.getString(R.string.player_error, errorEvent.code.value)
        hidePlayerControls()
    }

    private fun updateProgress() {
        binding.seekBar.post {
            var positionMs: Int
            var durationMs: Int

            if (player.isLive) {
                // The Seekbar does not support negative values
                // so the seekable range is shifted to the positive
                durationMs = (-player.maxTimeShift * 1000).toInt()
                positionMs = (durationMs + player.timeShift * 1000).toInt()

                if (playbackInfo.startTime > 0 && playbackInfo.endTime > 0) {
                    val currentTime = Calendar.getInstance()
                    val timePast = (currentTime.timeInMillis - playbackInfo.startTime).toInt()
                    val timeLeft = (playbackInfo.endTime - currentTime.timeInMillis).toInt()
                    binding.tvPosition.text = millisecondsToTimeString(timePast, false)
                    binding.tvDuration.text = millisecondsToTimeString(timeLeft, true)
                    durationMs = timePast + timeLeft
                    positionMs = timePast
                }

            } else {
                // Converting to milliseconds
                positionMs = (player.currentTime * 1000).toInt()
                durationMs = (player.duration * 1000).toInt()

                // Update the TextViews displaying the current position and duration
                binding.tvPosition.text = millisecondsToTimeString(positionMs, false)
                binding.tvDuration.text = millisecondsToTimeString(durationMs - positionMs, true)
            }

            // Update the values of the Seekbar
            binding.seekBar.progress = positionMs
            binding.seekBar.max = durationMs
        }
    }

    private fun addPlayerListeners() {
        player.on(PlayerEvent.TimeChanged::class.java, ::updateUI)
        player.on(PlayerEvent.Playing::class.java, ::updateUI)
        player.on(PlayerEvent.Paused::class.java, ::updateUI)
        player.on(PlayerEvent.StallStarted::class.java, ::updateUI)
        player.on(PlayerEvent.StallEnded::class.java, ::updateUI)
        player.on(PlayerEvent.Error::class.java, ::updateUI)
        player.on(PlayerEvent.CueEnter::class.java, ::updateUI)
        player.on(PlayerEvent.CueExit::class.java, ::updateUI)
        player.on(PlayerEvent.RenderFirstFrame::class.java, ::updateUI)
        player.on(SourceEvent.Loaded::class.java, ::updateUI)
        player.on(SourceEvent.Load::class.java, ::updateUI)
        player.on(SourceEvent.Error::class.java, ::updateUI)
    }

    private fun addListeners() {
        with(binding) {
            btnPlayPause.setOnClickListener { togglePlay() }

            btnRestart.setOnClickListener {
                if (!player.isLive)
                    player.seek(0 / 1000.0)
                else
                    player.timeShift((0 - binding.seekBar.max) / 1000.0)
            }

            btnCaptions.setOnClickListener {
                if (player.availableAudio.isNotEmpty() || player.availableSubtitles.isNotEmpty()) {
                    showCaptionsAndAudioOptions()
                }
            }

            btnBack.setOnClickListener { onBackButtonClick?.invoke() }

            btnMiniEpg.setOnClickListener { onMiniEpgButtonClick?.invoke() }

            seekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        }

        addPlayerListeners()
    }

    private fun togglePlay() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun showPlayerControls() {
        if (!playerControlShown) {
            playerControlShown = true
            setControlsVisible(true)
        }
    }

    fun hidePlayerControls() {
        if (playerControlShown) {
            playerControlShown = false
            setControlsVisible(false)
        }
    }

    fun isPlayerControlsShown() = playerControlShown

    fun setProgramDetails(
        title: String,
        fromTime: Long,
        toTime: Long,
        description: String?,
        channelImgURL: String?,
        timeFormat: TimeFormat
    ) {
        val startTime = Instant.fromEpochMilliseconds(fromTime).getTimeString(timeFormat)
        val endTime = Instant.fromEpochMilliseconds(toTime).getTimeString(timeFormat)
        binding.tvProgramTitle.text = title
        binding.tvSchedule.text = String.format("%s - %s • TVPG", startTime, endTime)
        binding.tvProgramDescription.text = description
        playbackInfo = playbackInfo.copy(startTime = fromTime, endTime = toTime)
        Glide.with(this)
            .load(channelImgURL)
            .into(binding.ivChannelLogo)
    }

    fun onStart() = playerView.onStart()

    fun onResume() = playerView.onResume()

    fun onPause() = playerView.onPause()

    fun onStop() = playerView.onStop()

    fun onDestroy() = playerView.onDestroy()

    private fun setControlsVisible(visible: Boolean) {
        with(binding) {
            // btnPlayPause.isVisible = visible
            btnBack.isVisible = visible
            btnMiniEpg.isVisible = visible
            btnCaptions.isVisible = visible
            //btnRecord.visibility = visible
            //btnRestart.visibility = visible
            seekBar.isVisible = visible
            tvPosition.isVisible = visible
            tvDuration.isVisible = visible
            ivChannelLogo.isVisible = visible
            tvProgramTitle.isVisible = visible
            tvSchedule.isVisible = visible
            tvProgramDescription.isVisible = visible
            playerTransparency.isVisible = visible
            liveTag.isVisible = visible && player.isLive
        }
    }

    private fun millisecondsToTimeString(milliseconds: Int, isTimeLeft: Boolean): String {
        val second = milliseconds / 1000 % 60
        val minute = milliseconds / (1000 * 60) % 60
        val hour = milliseconds / (1000 * 60 * 60) % 24
        return if (hour > 0) {
            if (isTimeLeft) {
                String.format("-%02d:%02d:%02d", hour, minute, second)
            } else {
                String.format("%02d:%02d:%02d", hour, minute, second)
            }
        } else {
            if (isTimeLeft) {
                String.format("-%02d:%02d", minute, second)
            } else {
                String.format("%02d:%02d", minute, second)
            }
        }
    }

    fun setZoom() {
        //LiveLayout
        binding.liveTag.layoutParams.width = resources.getDimension(R.dimen._82dp).toInt()
        binding.liveTag.layoutParams.height = resources.getDimension(R.dimen._32dp).toInt()
        //LiveDot
        binding.liveDot.layoutParams.width = resources.getDimension(R.dimen._14dp).toInt()
        binding.liveDot.layoutParams.height = resources.getDimension(R.dimen._14dp).toInt()
        //LiveText
        binding.liveText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        //ChannelLogo
        binding.ivChannelLogo.layoutParams.width = resources.getDimension(R.dimen._80dp).toInt()
        binding.ivChannelLogo.layoutParams.height = resources.getDimension(R.dimen._80dp).toInt()
        //ProgramTitle
        binding.tvProgramTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
        //Schedule
        binding.tvSchedule.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        //Description
        binding.tvProgramDescription.layoutParams.width = resources.getDimension(R.dimen._550dp).toInt()
        binding.tvProgramDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        //ButtonMiniEPG
        binding.btnMiniEpg.layoutParams.width = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        binding.btnMiniEpg.layoutParams.height = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        //ButtonCaptions
        binding.btnCaptions.layoutParams.width = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        binding.btnCaptions.layoutParams.height = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        //ButtonRecord
        binding.btnRecord.layoutParams.width = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        binding.btnRecord.layoutParams.height = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        //ButtonBack
        binding.btnBack.layoutParams.width = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        binding.btnBack.layoutParams.height = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        //ButtonRestart
        binding.btnRestart.layoutParams.width = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        binding.btnRestart.layoutParams.height = resources.getDimension(R.dimen.player_screen_zoomed_btn_width).toInt()
        //ButtonPlay
        binding.btnPlayPause.layoutParams.width = resources.getDimension(R.dimen._82dp).toInt()
        binding.btnPlayPause.layoutParams.height = resources.getDimension(R.dimen._82dp).toInt()

        //TextPosition
        binding.tvPosition.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        //TextDuration
        binding.tvDuration.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        //SeekBar
        (binding.seekBar.layoutParams as MarginLayoutParams).bottomMargin = resources.getDimension(R.dimen._40dp).toInt()
        // captions Text
        binding.subtitles.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
    }

    private fun showCaptionsAndAudioOptions() {
        val audios = arrayListOf<AudioTrack>().apply {
            add(AudioTrack("", "Off", id = CaptionsAndDescriptiveAudioDialogFragment.AUDIO_TRACK_OFF_ID))
            addAll(player.availableAudio)
        }
        val subtitles = player.availableSubtitles

        val dialog = CaptionsAndDescriptiveAudioDialogFragment.newInstance(
            audios,
            subtitles,
            if (player.isMuted) CaptionsAndDescriptiveAudioDialogFragment.AUDIO_TRACK_OFF_ID else player.audio?.id ?: "",
            player.subtitle?.id ?: ""
        )

        dialog.onTracksSelected = { audioTrackId, subTitleTrackId ->
            setAudioAndSubtitleTracks(audioTrackId, subTitleTrackId)
        }
        dialog.show(fragmentManager)
    }

    private fun setAudioAndSubtitleTracks(audioTrackId: String?, subTitleTrackId: String?) {
        Timber.e("===> add sub called $audioTrackId,=== $subTitleTrackId")
        if (audioTrackId == CaptionsAndDescriptiveAudioDialogFragment.AUDIO_TRACK_OFF_ID)
            player.mute()
        else {
            if (!audioTrackId.isNullOrBlank())
                player.setAudio(audioTrackId)
            player.unmute()
        }

        if (!subTitleTrackId.isNullOrBlank())
            player.setSubtitle(subTitleTrackId)
    }

    fun getSubtitleTrackId() = player.subtitle?.id

    fun getAudioTrackId() = if (player.isMuted)
        CaptionsAndDescriptiveAudioDialogFragment.AUDIO_TRACK_OFF_ID else player.audio?.id
}
