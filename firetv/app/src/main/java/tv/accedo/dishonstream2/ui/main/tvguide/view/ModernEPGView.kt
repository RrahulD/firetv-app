package tv.accedo.dishonstream2.ui.main.tvguide.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewModernEpgViewBinding
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.ui.view.FocusableLockFrameLayout


class ModernEPGView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val UPDATE_MILLIS = 60 * 1000 // 1 minute.
        private const val EPG_OFFSCREEN_OFFSET_REFRESH = 4
        private const val TAG = "ModernEpgView :: "
    }

    private val binding: ViewModernEpgViewBinding =
        ViewModernEpgViewBinding.inflate(LayoutInflater.from(context), this, true)

    val recyclerView = binding.programs

    private val runnableUpdate: Runnable = object : Runnable {
        override fun run() {
            refreshModerEpgContent()
        }
    }

    private fun refreshModerEpgContent() {
        val firstVisibleItemPos =
            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val lastVisibleItemPos =
            (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        recyclerView.adapter?.let {
            val first =
                if (firstVisibleItemPos - EPG_OFFSCREEN_OFFSET_REFRESH >= 0) firstVisibleItemPos - EPG_OFFSCREEN_OFFSET_REFRESH else 0
            val last =
                if (lastVisibleItemPos + EPG_OFFSCREEN_OFFSET_REFRESH < it.itemCount) {
                    lastVisibleItemPos + EPG_OFFSCREEN_OFFSET_REFRESH
                } else {
                    it.itemCount.minus(1)
                }
            Timber.tag(TAG).d(
                "updating epg from : firstVisibleItemPos : $firstVisibleItemPos, lastVisibleItemPos : $lastVisibleItemPos,first : $first, last : $last"
            )
            it.notifyItemRangeChanged(first, last)
            postDelayed(runnableUpdate, getNextUpdateDelay())
        }
    }

    private fun getNextUpdateDelay(): Long {
        val timeSinceLastUpdate = System.currentTimeMillis() % UPDATE_MILLIS
        return UPDATE_MILLIS - timeSinceLastUpdate
    }

    init {
        binding.programs.layoutManager = CenterLayoutManager(this.context)
    }

    val focusChangeListener =
        OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val focussedPos = recyclerView.getChildLayoutPosition(recyclerView.focusedChild)
                val firstVisibleItemPos =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val lastVisibleItemPos =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (focussedPos >= lastVisibleItemPos - 1 || focussedPos <= firstVisibleItemPos + 1)
                    recyclerView.smoothScrollToPosition(focussedPos)
            }
        }

    fun setDataSource(dataSource: DataSource) {
        binding.programs.adapter = ListProgramAdapter(dataSource)
        if (hasWindowFocus()) postDelayed(runnableUpdate, getNextUpdateDelay())
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            refreshModerEpgContent()
            postDelayed(runnableUpdate, getNextUpdateDelay())
        } else {
            removeCallbacks(runnableUpdate)
        }
    }

    class ProgramAdapter(private val dataSource: DataSource, private val channel: Channel) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val programs = channel.programs.filter { !it.hasProgramEnded() }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return dataSource.onCreateProgramViewHolder(parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            dataSource.onBindProgram(
                holder as DataSource.ProgramViewHolder,
                channel,
                programs[position],
                position
            )
        }

        override fun getItemCount(): Int {
            return programs.size
        }
    }

    private class ListProgramAdapter(private val dataSource: DataSource) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return dataSource.onCreateListProgramViewHolder(parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            dataSource.onBindListProgram(
                holder as DataSource.ListProgramViewHolder, dataSource.getChannels()[position],
                ProgramAdapter(dataSource, dataSource.getChannels()[position])
            )
        }

        override fun getItemCount(): Int {
            return dataSource.getChannels().size
        }
    }

    abstract class DataSource(private val isZoomed: Boolean) {

        abstract fun getStartTimeMillis(program: Program): Long

        abstract fun getEndTimeMillis(program: Program): Long

        abstract fun onBindProgram(programViewHolder: ProgramViewHolder, channel: Channel, program: Program, position: Int)

        abstract fun onBindListProgram(
            listProgramViewHolder: ListProgramViewHolder,
            channel: Channel,
            adapter: ProgramAdapter
        )

        abstract fun getChannels(): List<Channel>

        fun onCreateProgramViewHolder(viewGroup: ViewGroup): ProgramViewHolder {
            val layout = if (isZoomed) R.layout.view_epg_zoomed_modern_program else R.layout.view_epg_modern_program
            return ProgramViewHolder(inflate(viewGroup, layout), isZoomed)
        }

        fun onCreateListProgramViewHolder(viewGroup: ViewGroup): ListProgramViewHolder {
            val layout = if (isZoomed) R.layout.view_zoomed_program_list else R.layout.view_program_list
            return ListProgramViewHolder(inflate(viewGroup, layout))
        }

        class ProgramViewHolder(view: View, isZoomed: Boolean) : RecyclerView.ViewHolder(view) {
            val ivProgram: ImageView = view.findViewById(R.id.program_image)
            val tvDuration: TextView = view.findViewById(R.id.tv_duration)
            val ivPlayIcon: ImageView = view.findViewById(R.id.iv_play_icon)
            val textView: TextView = view.findViewById(R.id.title_program)
            val progressBar: ProgressBar = view.findViewById(R.id.progress_bar_program)
            val focusableView: FocusableLockFrameLayout = view.findViewById(R.id.parentView)
            lateinit var tvProgramNumber: TextView
            lateinit var tvTimeLeft: TextView

            init {
                if (!isZoomed) {
                    tvTimeLeft = view.findViewById(R.id.tv_time_left)
                    tvProgramNumber = view.findViewById(R.id.tv_episode_number)
                }
            }

        }

        class ListProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val listProgram: RecyclerView = view.findViewById(R.id.list_programs)
            val ivChannel: ImageView = view.findViewById(R.id.iv_modern_channel)
            val textChannel: TextView = view.findViewById(R.id.text_channel)

            init {
                listProgram.layoutManager = LinearLayoutManager(listProgram.context, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        protected fun inflate(viewGroup: ViewGroup, layoutId: Int): View {
            return LayoutInflater.from(viewGroup.context).inflate(layoutId, viewGroup, false)
        }
    }
}