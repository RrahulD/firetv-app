package tv.accedo.dishonstream2.ui.main.tvguide.classicepg

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import hu.accedo.commons.tools.Callback
import hu.accedo.commons.widgets.epg.EpgDataSource
import hu.accedo.commons.widgets.epg.EpgView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.usecase.program.GetProgramInfoUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.extensions.getTimeString
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.theme.ThemeManager
import java.util.*
import kotlin.collections.HashMap

@OptIn(KoinApiExtension::class)
class DataSource(
    private var channels: MutableList<Channel>,
    epgView: EpgView,
    private val timeFormat: TimeFormat,
    private val isZoomed: Boolean,
    private val onProgramClick: (Channel, Program) -> Unit
) : EpgDataSource<Channel, Program>(epgView), KoinComponent {

    private val getProgramInfoUseCase: GetProgramInfoUseCase by inject()
    private val themeManager: ThemeManager by inject()
    private val dataSourceScope = CoroutineScope(Dispatchers.Main)

    companion object {
        const val KEY_VIEW_RIGHT_FOCUSSED_TAG = 111111111
    }

    override fun getStartTimeMillis(program: Program, channel: Channel): Long {
        return if (channel.programs.indexOf(program) == 0) {
            program.startTimeFake
        } else {
            return program.startTime
        }
    }

    override fun getEndTimeMillis(program: Program): Long {
        return program.endTime
    }

    override fun onRequestChannels(callback: Callback<MutableList<Channel>>): Any {
        return callback.execute(channels)
    }

    override fun onRequestData(
        channels: MutableList<Channel>,
        fromDate: Long,
        toDate: Long,
        callback: Callback<MutableMap<Channel, MutableList<Program>>>
    ): Any {

        val map: MutableMap<Channel, MutableList<Program>> = HashMap()
        for (channel in this.channels) {
            map[channel] = channel.programs.toMutableList()
        }

        return callback.execute(map)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindProgram(viewHolderProgram: ViewHolderProgram, channel: Channel, program: Program) {
        val vh = viewHolderProgram as tv.accedo.dishonstream2.ui.main.tvguide.classicepg.ViewHolderProgram
        vh.playIcon.isVisible = program.isLive()
        vh.textView.text = program.name
        // if programme is last scheduled for that channel block right key focus
        if (program == channel.programs[channel.programs.size.minus(1)]) {
            vh.focusableView.consumeRightKeyEvent()
            vh.focusableView.setTag(KEY_VIEW_RIGHT_FOCUSSED_TAG, true)
        } else if (vh.focusableView.getTag(KEY_VIEW_RIGHT_FOCUSSED_TAG) == true) {
            // remove the tag if same view is recycled and being used in different position
            vh.focusableView.setTag(KEY_VIEW_RIGHT_FOCUSSED_TAG, false)
            vh.focusableView.setOnKeyListener(null)
        }
        if (!isZoomed) {
            dataSourceScope.launch {
                try {
                    vh.tvProgramNumber?.text = ""
                    val programDetail = getProgramInfoUseCase(program.echoStarId)
                    vh.textView.text = programDetail.seriesTitle
                    vh.tvProgramNumber?.text = programDetail.title
                } catch (ex: Exception) {
                    Timber.e(ex)
                }
            }
        }
        themeManager.getEpgItemBackgroundDrawable()?.let { viewHolderProgram.itemView.background = it }
        viewHolderProgram.itemView.setOnClickListener {
            onProgramClick(channel, program)
        }
    }

    override fun onBindChannel(viewHolderChannel: ViewHolderChannel, channel: Channel) {
        if (viewHolderChannel is tv.accedo.dishonstream2.ui.main.tvguide.classicepg.ViewHolderChannel) {
            if (channel.detail == null) {
                viewHolderChannel.channelTextView.text = channel.name
                viewHolderChannel.channelTextView.show()
                viewHolderChannel.imageView.hide()
            } else {
                viewHolderChannel.imageView.show()
                viewHolderChannel.channelTextView.hide()
                Glide.with(viewHolderChannel.imageView.context)
                    .load(channel.detail?.logoURL)
                    .into(viewHolderChannel.imageView)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindTimeBar(viewHolderTimebar: ViewHolderTimebar, timestamp: Long) {
        if (timestamp >= 0)
            viewHolderTimebar.textView.text =
                Instant.fromEpochMilliseconds(timestamp).getTimeString(timeFormat, TimeZone.getTimeZone("UTC"))
    }

    override fun onCreateChannelViewHolder(viewGroup: ViewGroup?): ViewHolderChannel {
        return tv.accedo.dishonstream2.ui.main.tvguide.classicepg.ViewHolderChannel(
            LayoutInflater.from(viewGroup!!.context).inflate(R.layout.view_epg_channel, viewGroup, false)
        )
    }

    override fun onCreateTimeBarViewHolder(viewGroup: ViewGroup?): ViewHolderTimebar {
        return ViewHolderTimeBar(
            LayoutInflater.from(viewGroup!!.context).inflate(R.layout.view_epg_timebar, viewGroup, false),
            isZoomed
        )
    }

    override fun onCreateProgramViewHolder(viewGroup: ViewGroup?): ViewHolderProgram {
        val classicLayout = if (isZoomed) R.layout.view_epg_zoomed_program else R.layout.view_epg_program
        return ViewHolderProgram(LayoutInflater.from(viewGroup!!.context).inflate(classicLayout, viewGroup, false), isZoomed)
    }

    override fun onCreateHairlineViewHolder(viewGroup: ViewGroup?): ViewHolderHairline {
        return tv.accedo.dishonstream2.ui.main.tvguide.classicepg.ViewHolderHairline(
            LayoutInflater.from(viewGroup!!.context).inflate(R.layout.view_epg_hairline, viewGroup, false)
        )
    }

    override fun onCreatePlaceholderViewHolder(viewGroup: ViewGroup?): ViewHolderPlaceholder {
        return ViewHolderPlaceHolder(
            LayoutInflater.from(viewGroup!!.context).inflate(R.layout.view_epg_placeholder, viewGroup, false)
        )
    }
}