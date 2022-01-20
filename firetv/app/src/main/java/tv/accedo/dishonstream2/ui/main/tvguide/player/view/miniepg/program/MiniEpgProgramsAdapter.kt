package tv.accedo.dishonstream2.ui.main.tvguide.player.view.miniepg.program

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.databinding.ViewMiniEpgProgramBinding
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program

class MiniEpgProgramsAdapter(
    private val channel: Channel,
    private val onFocus: (Int) -> Unit,
    private val onWatchProgram: (Channel, Program) -> Unit,
    private val channelPosition: Int
) : RecyclerView.Adapter<MiniEpgProgramViewHolder>() {

    // In the miniEPG we will show upto 2 programs per channel
    private val programs = channel.programs.subList(0, minOf(channel.programs.size, 2))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniEpgProgramViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MiniEpgProgramViewHolder(ViewMiniEpgProgramBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MiniEpgProgramViewHolder, position: Int) {
        holder.bind(
            channel,
            programs[position],
            isFirstProgram = position == 0,
            isLastProgram = position == itemCount - 1,
            onWatchProgram,
            onFocus,
            channelPosition
        )
    }

    override fun onViewAttachedToWindow(holder: MiniEpgProgramViewHolder) {
        holder.attach()
    }

    override fun onViewDetachedFromWindow(holder: MiniEpgProgramViewHolder) {
        holder.detach()
    }

    override fun getItemCount(): Int = programs.size
}