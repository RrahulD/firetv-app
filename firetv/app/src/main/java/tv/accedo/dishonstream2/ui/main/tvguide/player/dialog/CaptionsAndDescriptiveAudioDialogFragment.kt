package tv.accedo.dishonstream2.ui.main.tvguide.player.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitmovin.player.api.media.Track
import com.bitmovin.player.api.media.audio.AudioTrack
import com.bitmovin.player.api.media.subtitle.SubtitleTrack
import org.koin.android.ext.android.inject
import tv.accedo.dishonstream2.databinding.CaptionsAndAudioItemViewBinding
import tv.accedo.dishonstream2.databinding.ViewCaptionsAndDescriptiveAudioDialogBinding
import tv.accedo.dishonstream2.extensions.capitalise
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.tvguide.player.dialog.CaptionsAndDescriptiveAudioDialogFragment.CaptionAndDescriptiveAudioAdapter.CaptionsAndAudioViewHolder
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class CaptionsAndDescriptiveAudioDialogFragment : BaseFullScreenDialogFragment() {

    companion object {
        private const val KEY_AUDIO_TRACKS = "audio_tracks"
        private const val KEY_SUBTITLE_TRACKS = "subtitle_tracks"
        private const val KEY_SELECTED_AUDIO_TRACK = "selected_audio_tracks"
        private const val KEY_SELECTED_SUBTITLE_TRACK = "selected_subtitle_tracks"

        const val AUDIO_TRACK_OFF_ID = "audio-off--1"

        fun newInstance(
            audioTracks: List<AudioTrack>,
            subtitleTracks: List<SubtitleTrack>,
            selectedAudioTrack: String,
            selectedSubtitleTrack: String
        ) =
            CaptionsAndDescriptiveAudioDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray(KEY_AUDIO_TRACKS, audioTracks.toTypedArray())
                    putParcelableArray(KEY_SUBTITLE_TRACKS, subtitleTracks.toTypedArray())
                    putString(KEY_SELECTED_AUDIO_TRACK, selectedAudioTrack)
                    putString(KEY_SELECTED_SUBTITLE_TRACK, selectedSubtitleTrack)
                }
            }
    }

    private lateinit var binding: ViewCaptionsAndDescriptiveAudioDialogBinding
    private val themeManager: ThemeManager by inject()
    private var selectedAudioTrackId: String = ""
    private var selectedSubTitleTrackId: String = ""

    var onTracksSelected: ((String, String) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ViewCaptionsAndDescriptiveAudioDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val audioTracks =
            requireArguments().getParcelableArray(KEY_AUDIO_TRACKS)?.filterIsInstance<AudioTrack>() ?: emptyList()

        val subTitleTracks =
            requireArguments().getParcelableArray(KEY_SUBTITLE_TRACKS)?.filterIsInstance<SubtitleTrack>() ?: emptyList()

        selectedAudioTrackId = requireArguments().getString(KEY_SELECTED_AUDIO_TRACK, "")
        selectedSubTitleTrackId = requireArguments().getString(KEY_SELECTED_SUBTITLE_TRACK, "")

        binding.audioList.adapter =
            CaptionAndDescriptiveAudioAdapter(audioTracks)

        binding.captionList.adapter =
            CaptionAndDescriptiveAudioAdapter(subTitleTracks)

        binding.btnAction.setOnClickListener {
            onTracksSelected?.invoke(selectedAudioTrackId, selectedSubTitleTrackId)
            dismiss()
        }

        themeManager.getPrimaryButtonBackgroundDrawable()?.let {
            binding.btnAction.background = it
        }
    }

    private inner class CaptionAndDescriptiveAudioAdapter(
        private val items: List<Track>
    ) : RecyclerView.Adapter<CaptionsAndAudioViewHolder>() {

        private var lastChecked: View? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaptionsAndAudioViewHolder {
            return CaptionsAndAudioViewHolder(
                CaptionsAndAudioItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: CaptionsAndAudioViewHolder, position: Int) {
            themeManager.getPrimaryButtonBackgroundDrawable()?.let { holder.itemView.background = it }
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        private inner class CaptionsAndAudioViewHolder(private val binding: CaptionsAndAudioItemViewBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("NotifyDataSetChanged")
            fun bind(track: Track) {
                binding.optionText.text = track.label?.capitalise()
                val isTrackSelectedPreviously = when (track) {
                    is AudioTrack -> track.id == selectedAudioTrackId
                    is SubtitleTrack -> track.id == selectedSubTitleTrackId
                    else -> false
                }

                if (isTrackSelectedPreviously) {
                    binding.check.show()
                    lastChecked = binding.check
                }

                binding.root.setOnClickListener {
                    when (track) {
                        is AudioTrack -> selectedAudioTrackId = track.id
                        is SubtitleTrack -> selectedSubTitleTrackId = track.id
                    }
                    lastChecked?.hide()
                    binding.check.show()
                    lastChecked = binding.check
                }
            }
        }
    }
}