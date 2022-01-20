package tv.accedo.dishonstream2.ui.main.tvguide.player.view.miniepg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.databinding.ViewMiniEpgChannelBinding
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.ui.main.tvguide.player.view.miniepg.program.MiniEpgProgramsAdapter


class MiniEpgAdapter(
    private val channels: List<Channel>,
    private val onWatchProgram: (Channel, Program) -> Unit,
    private val onChannelFocused: ((Int) -> Unit)
) : RecyclerView.Adapter<MiniEpgAdapter.MiniEpgViewHolder>() {

    inner class MiniEpgViewHolder(
        private val binding: ViewMiniEpgChannelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(channel: Channel, position: Int) {
            binding.root.adapter = MiniEpgProgramsAdapter(channel, onChannelFocused, onWatchProgram, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniEpgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MiniEpgViewHolder(ViewMiniEpgChannelBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MiniEpgViewHolder, position: Int) {
        holder.bind(channels[position], position)
    }

    override fun getItemCount(): Int = channels.size
}