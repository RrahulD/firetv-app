package tv.accedo.dishonstream2.ui.main.tvguide.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.buffer.BufferConfig
import com.bitmovin.player.api.buffer.BufferMediaTypeConfig
import com.bitmovin.player.api.drm.DrmConfig
import com.bitmovin.player.api.drm.WidevineConfig
import com.bitmovin.player.api.event.Event
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.media.AdaptationConfig
import com.bitmovin.player.api.source.SourceConfig
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import timber.log.Timber
import tv.accedo.dishonstream2.databinding.ViewPipBinding
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.tvguide.player.model.PlaybackInfo

@OptIn(KoinApiExtension::class)
@SuppressLint("ViewConstructor")
class PIPView constructor(
    context: Context,
    private val playbackInfo: PlaybackInfo
) : FrameLayout(context), KoinComponent {

    var binding: ViewPipBinding = ViewPipBinding.inflate(LayoutInflater.from(context), this, true)

    private val player = Player.create(
        context,
        PlayerConfig(key = playbackInfo.playerLicense).apply {
            bufferConfig = BufferConfig(BufferMediaTypeConfig(15.0))
            adaptationConfig = AdaptationConfig(maxSelectableVideoBitrate = 256000)
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val drmConfig = DrmConfig.Builder(
            playbackInfo.drmLicenseURL,
            WidevineConfig.UUID,
            hashMapOf("PreAuthorization" to playbackInfo.drmToken)
        ).build()
        addPlayerListeners()
        val sourceConfig = SourceConfig.fromUrl(url = playbackInfo.dashURL)
        sourceConfig.drmConfig = drmConfig
        player.load(sourceConfig)
    }

    override fun onDetachedFromWindow() {
        player.off(::updateUI)
        player.destroy()
        super.onDetachedFromWindow()
    }

    fun onStart() = playerView.onStart()

    fun onResume() = playerView.onResume()

    fun onPause() = playerView.onPause()

    fun onStop() = playerView.onStop()

    fun onDestroy() = playerView.onDestroy()

    private fun updateUI(event: Event) {
        when (event) {
            is PlayerEvent.Playing -> binding.progressBar.hide()

            is PlayerEvent.StallStarted -> binding.progressBar.show()

            is PlayerEvent.StallEnded -> binding.progressBar.hide()

            is PlayerEvent.Error -> {
                Timber.e(event.message)
                binding.progressBar.hide()
                binding.errorMessage.show()
                binding.errorMessage.text = event.message
            }

            is SourceEvent.Load -> binding.progressBar.show()

            is SourceEvent.Loaded -> {
                binding.progressBar.hide()
                player.play()
            }

            is SourceEvent.Error -> binding.progressBar.hide()

            else -> {
            }
        }
    }

    private fun addPlayerListeners() {
        player.on(PlayerEvent.Playing::class.java, ::updateUI)
        player.on(PlayerEvent.StallStarted::class.java, ::updateUI)
        player.on(PlayerEvent.StallEnded::class.java, ::updateUI)
        player.on(PlayerEvent.Error::class.java, ::updateUI)
        player.on(SourceEvent.Loaded::class.java, ::updateUI)
        player.on(SourceEvent.Load::class.java, ::updateUI)
        player.on(SourceEvent.Error::class.java, ::updateUI)
    }


    companion object {
        const val TAG = "PIPView"
    }

}