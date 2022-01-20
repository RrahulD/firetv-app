package tv.accedo.dishonstream2.ui.main.tvguide.classicepg

import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTimeFormatUseCase
import tv.accedo.dishonstream2.extensions.*
import tv.accedo.dishonstream2.ui.base.viewholder.itemdecoration.HorizontalSpaceItemDecoration
import tv.accedo.dishonstream2.ui.main.tvguide.view.ModernEPGView
import tv.accedo.dishonstream2.ui.theme.ThemeManager
import java.util.*

@OptIn(KoinApiExtension::class)
class ModernDataSource(
    private var channels: List<Channel>,
    private val isZoomed: Boolean,
    private val modernEpgView: ModernEPGView,
    private val onProgramClick: (Channel, Program) -> Unit
) : ModernEPGView.DataSource(isZoomed), KoinComponent {

    private val getProgramInfoUseCase: GetProgramInfoUseCase by inject()
    private val getTimeFormatUseCase: GetTimeFormatUseCase by inject()
    private val themeManager: ThemeManager by inject()
    private val dataSourceScope = CoroutineScope(Dispatchers.Main)
    private val horizontalSpaceItemDecoration =
        HorizontalSpaceItemDecoration(modernEpgView.context.resources.getDimensionPixelSize(R.dimen._8dp))

    companion object {
        const val KEY_VIEW_KEY_LISTENER_FOCUSSED_TAG = 111111111
        const val KEY_VIEW_FOCUS_UP_TAG = 111111112
    }

    override fun getStartTimeMillis(program: Program): Long {
        return program.startTime
    }

    override fun getEndTimeMillis(program: Program): Long {
        return program.endTime
    }

    override fun onBindProgram(
        programViewHolder: ProgramViewHolder,
        channel: Channel,
        program: Program,
        position: Int
    ) {
        programViewHolder.textView.text = program.name
        programViewHolder.itemView.onFocusChangeListener = modernEpgView.focusChangeListener
        themeManager.getEpgItemBackgroundDrawable()?.let {
            programViewHolder.itemView.background = it
        }

        dataSourceScope.launch {
            val timeFormat = getTimeFormatUseCase()
            val startTimeString = Instant.fromEpochMilliseconds(program.startTime).getTimeString(timeFormat)
            val endTimeString = Instant.fromEpochMilliseconds(program.endTime).getTimeString(timeFormat)
            programViewHolder.tvDuration.text = String.format("%s - %s", startTimeString, endTimeString)
        }
        // for all programmes associated with first channel in epg grid we are setting its focus up on guide btn
        if (channels[0].programs.contains(program)) {
            programViewHolder.focusableView.nextFocusUpId = R.id.tv_guide
            programViewHolder.focusableView.setTag(KEY_VIEW_FOCUS_UP_TAG, true)
        } else if (programViewHolder.focusableView.getTag(KEY_VIEW_FOCUS_UP_TAG) == true) {
            // remove the tag if same view is recycled and being used in different position
            programViewHolder.focusableView.setTag(KEY_VIEW_FOCUS_UP_TAG, false)
            programViewHolder.focusableView.nextFocusUpId = View.NO_ID
        }

        // for all programmes which are on leftmost side of grid at 1st column we are setting their focus left as null
        when {
            position == 0 -> {
                programViewHolder.focusableView.consumeLeftKeyEvent()
                programViewHolder.focusableView.setTag(KEY_VIEW_KEY_LISTENER_FOCUSSED_TAG, true)
            }
            program == channel.programs[channel.programs.size - 1] -> {
                // if programme schedule is at the last for associated channel we are settings its right focus as null
                programViewHolder.focusableView.consumeRightKeyEvent()
                programViewHolder.focusableView.setTag(KEY_VIEW_KEY_LISTENER_FOCUSSED_TAG, true)
            }
            programViewHolder.focusableView.getTag(KEY_VIEW_KEY_LISTENER_FOCUSSED_TAG) == true -> {
                // remove the tag if same view is recycled and being used in different position
                programViewHolder.focusableView.setTag(KEY_VIEW_KEY_LISTENER_FOCUSSED_TAG, false)
                programViewHolder.focusableView.setOnKeyListener(null)
            }
        }

        programViewHolder.ivPlayIcon.isVisible = program.isLive()
        programViewHolder.progressBar.isVisible = program.isLive()

        if (program.isLive()) {
            programViewHolder.progressBar.progress = program.getProgramCompletionPercent()
            programViewHolder.progressBar.show()
            if (!isZoomed) {
                programViewHolder.tvTimeLeft.text = " • ${program.getRemainingTimeInMinutes()}M LEFT"
                programViewHolder.tvTimeLeft.show()
            }
        } else {
            programViewHolder.progressBar.hide()
            if (!isZoomed) programViewHolder.tvTimeLeft.hide()
        }

        dataSourceScope.launch {
            try {
                val programDetail = getProgramInfoUseCase(program.echoStarId)
                if (!isZoomed)
                    programViewHolder.tvProgramNumber.text = programDetail.title
                programViewHolder.textView.text = programDetail.seriesTitle

                Glide.with(programViewHolder.ivProgram.context)
                    .load(programDetail.richMediaImageInfo?.imageURL)
                    .apply(RequestOptions.overrideOf(600, 300))
                    .error(R.drawable.dish_image_not_found)
                    .into(programViewHolder.ivProgram)
            } catch (ex: Exception) {
                programViewHolder.ivProgram.setImageResource(R.drawable.dish_image_not_found)
                Timber.e(ex)
            }
        }

        programViewHolder.itemView.setOnClickListener {
            onProgramClick(channel, program)
        }
    }

    override fun getChannels(): List<Channel> {
        return channels
    }

    override fun onBindListProgram(
        listProgramViewHolder: ListProgramViewHolder,
        channel: Channel,
        adapter: ModernEPGView.ProgramAdapter
    ) {
        listProgramViewHolder.listProgram.adapter = adapter
        listProgramViewHolder.listProgram.removeItemDecoration(horizontalSpaceItemDecoration)
        listProgramViewHolder.listProgram.addItemDecoration(horizontalSpaceItemDecoration)
        if (channel.detail == null) {
            listProgramViewHolder.textChannel.show()
            listProgramViewHolder.ivChannel.hide()
            listProgramViewHolder.textChannel.text = channel.name
        } else {
            listProgramViewHolder.textChannel.hide()
            listProgramViewHolder.ivChannel.show()
            Glide.with(listProgramViewHolder.ivChannel.context)
                .load(channel.detail?.logoURL)
                .into(listProgramViewHolder.ivChannel)
        }
    }

    private fun getProgramProgress(program: Program): Long {
        val currentTime = Calendar.getInstance().timeInMillis
        return (currentTime - program.startTime)
    }
}