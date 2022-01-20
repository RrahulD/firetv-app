package tv.accedo.dishonstream2.ui.main.tvguide.player.view.miniepg

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewMinEpgBinding
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.ui.base.viewholder.itemdecoration.VerticalSpaceItemDecoration

@Suppress("ViewConstructor")
class MiniEPGView constructor(
    context: Context,
    channels: List<Channel>,
    onWatchProgram: (Channel, Program) -> Unit
) : FrameLayout(context) {

    private val binding = ViewMinEpgBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.channelsList.layoutManager = LinearLayoutManager(binding.root.context)
        binding.channelsList
            .addItemDecoration(VerticalSpaceItemDecoration(binding.root.context.resources.getDimensionPixelSize(R.dimen._20dp)))

        binding.channelsList.adapter =
            MiniEpgAdapter(channels, onWatchProgram) { position ->
                val scrollPosition = if (position < 0) 0 else if (position >= channels.size) channels.size - 1 else position
                binding.channelsList.smoothScrollToPosition(scrollPosition)
            }
    }
}