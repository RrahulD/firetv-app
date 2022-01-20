package tv.accedo.dishonstream2.ui.main.tvguide.player.view.miniepg.program

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import kotlinx.datetime.Instant
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewMiniEpgProgramBinding
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.extensions.getTimeString
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.ui.base.resource.Resource
import tv.accedo.dishonstream2.ui.base.viewholder.BaseLifeCycleViewHolder
import tv.accedo.dishonstream2.ui.main.search.ParentalPinChallenge
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsViewModel
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class MiniEpgProgramViewHolder(
    private val binding: ViewMiniEpgProgramBinding
) : BaseLifeCycleViewHolder(binding.root), KoinComponent {

    private val miniEpgProgramViewHolderViewModel: MiniEpgProgramViewHolderViewModel by inject { parametersOf(getViewHolderScope()) }
    private val sharedAppViewModel: SharedAppViewModel by inject()
    private val parentalControlsViewModel: ParentalControlsViewModel by inject { parametersOf(getViewHolderScope()) }
    private val themeManager: ThemeManager by inject()
    private var rating: String? = ""
    fun bind(
        channel: Channel,
        program: Program,
        isFirstProgram: Boolean,
        isLastProgram: Boolean,
        onWatchProgram: (Channel, Program) -> Unit,
        onFocus: ((Int) -> Unit), channelPosition: Int
    ) {
        with(binding) {
            title.text = program.name

            Glide.with(root.context)
                .load(channel.detail?.logoURL)
                .into(channelLogo)

            miniEpgProgramViewHolderViewModel.timeFormatLiveData.observe(this@MiniEpgProgramViewHolder) {
                val startTime = Instant.fromEpochMilliseconds(program.startTime).getTimeString(it)
                val endTime = Instant.fromEpochMilliseconds(program.endTime).getTimeString(it)
                subtitle.text =
                    String.format("%s • %s - %s", if (program.isLive()) "ON NOW" else "UP NEXT", startTime, endTime)
            }

            miniEpgProgramViewHolderViewModel.programDetailLiveData.observe(this@MiniEpgProgramViewHolder) {
                progressBar.isVisible = it is Resource.Loading

                when (it) {
                    is Resource.Success -> {
                        description.text = it.data.description
                        rating = it.data.rating
                    }
                    is Resource.Failure -> description.text = "Unable to load the details."
                    else -> {
                    }
                }
            }

            root.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) onFocus(channelPosition + 1)
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) onFocus(channelPosition - 1)
                false
            }

            root.setOnClickListener {
                val activity = binding.root.context as Activity
                val pinChallenge = ParentalPinChallenge(rating,
                     parentalControlsViewModel,
                    activity as FragmentActivity
                ) { context, challengeCallback -> }.setOnPassedListener {
                    onWatchProgram(channel, program)
                }
                pinChallenge.run()
            }

            root.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    onFocus.invoke(channelPosition)
                    miniEpgProgramViewHolderViewModel.loadProgramDetails(program.echoStarId)
                }
                updateViewStyleBasedOnFocusAndState(hasFocus, isFirstProgram, isLastProgram, program.isLive())
            }

            if (root.isFocused)
                miniEpgProgramViewHolderViewModel.loadProgramDetails(program.echoStarId)
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(this, { enabled ->
            if (enabled) {
                setZoom()
            }
        })
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        binding.subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)
        binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)

        binding.channelLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._50dp).toInt()
        binding.channelLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._50dp).toInt()

        binding.rightArrow.layoutParams.width = binding.root.resources.getDimension(R.dimen._30dp).toInt()
        binding.rightArrow.layoutParams.height = binding.root.resources.getDimension(R.dimen._30dp).toInt()

        binding.leftArrow.layoutParams.width = binding.root.resources.getDimension(R.dimen._30dp).toInt()
        binding.leftArrow.layoutParams.height = binding.root.resources.getDimension(R.dimen._30dp).toInt()

        binding.watchButton.layoutParams.width = binding.root.resources.getDimension(R.dimen._50dp).toInt()
        binding.watchButton.layoutParams.height = binding.root.resources.getDimension(R.dimen._50dp).toInt()
    }


    private fun updateViewStyleBasedOnFocusAndState(
        hasFocus: Boolean,
        isFirstProgram: Boolean,
        isLastProgram: Boolean,
        isLive: Boolean
    ) {
        with(binding) {
            if (progressBar.isVisible && !hasFocus)
                progressBar.hide()

            title.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    if (hasFocus) R.color.header_text_active else R.color.header_text_default
                )
            )

            watchButton.background = if (hasFocus) {
                ContextCompat.getDrawable(root.context, R.drawable.player_control_selected).apply {
                    val primaryColor = themeManager.getPrimaryColor()
                    if (this is GradientDrawable && primaryColor != null)
                        this.setColor(primaryColor)
                }
            } else {
                ContextCompat.getDrawable(root.context, R.drawable.player_control_unselected)
            }

            detailSection.isVisible = hasFocus
            watchButton.isVisible = hasFocus && isLive
            rightArrow.isVisible = hasFocus && !isLastProgram
            leftArrow.isVisible = hasFocus && !isFirstProgram
        }
    }
}
