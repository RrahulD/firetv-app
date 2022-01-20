package tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.compat.ViewModelCompat.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.CarouselWidgetLiveChannelLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.LiveChannelWidget
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.resource.Resource
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.programinfo.ProgramInfoDialogFragment
import tv.accedo.dishonstream2.ui.main.search.ParentalPinChallenge
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlCreatePinViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications.PermittedClassificationsViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.player.PlayerActivity
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class LiveChannelCarouselWidgetViewHolder(
    private val binding: CarouselWidgetLiveChannelLayoutBinding,
    private val fragmentManager: FragmentManager
) : WidgetViewHolder(binding.root), KoinComponent {

    private val liveChannelWidgetViewModel: LiveChannelWidgetViewModel by inject { parametersOf(getViewHolderScope()) }
    private val themeManager: ThemeManager by inject()
    private val parentalControlsViewModel: ParentalControlsViewModel by inject { parametersOf(getViewHolderScope()) }

    fun populate(widget: LiveChannelWidget, carouselBackgroundLoader: ((String?) -> Unit)?) {
        liveChannelWidgetViewModel.liveChannelCurrentProgramDetails.observe(this) {
            when (it) {
                is Resource.Failure -> populateError(carouselBackgroundLoader)
                is Resource.Loading -> {
                    // TODO: show loading?
                }
                is Resource.Success -> with(it.data) {
                    populateProgramDetails(
                        channel,
                        program,
                        programInfo,
                        carouselBackgroundLoader
                    )
                }
            }
        }
        liveChannelWidgetViewModel.loadCurrentProgramDetails(widget.channelId)
    }

    private fun populateProgramDetails(
        channel: Channel,
        program: Program,
        programDetail: ProgramInfo?,
        carouselBackgroundLoader: ((String?) -> Unit)?
    ) {
        hideAllViews()
        with(binding) {
            programTitle.text = program.name
            subtitle.text = programDetail?.description ?: ""
            Glide.with(root)
                .load(channel.detail?.logoURL)
                .into(channelLogo)

            container.setOnFocusChangeListener { _, hasFocus ->
                updateWatchButtonBackground(hasFocus)
                if (hasFocus)
                    carouselBackgroundLoader?.invoke(
                        programDetail?.richMediaImageInfo?.cdn16x9ImageUrl ?: programDetail?.richMediaImageInfo?.imageURL
                    )
            }
            updateWatchButtonBackground(container.isFocused)
            if (container.isFocused)
                carouselBackgroundLoader?.invoke(
                    programDetail?.richMediaImageInfo?.cdn16x9ImageUrl ?: programDetail?.richMediaImageInfo?.imageURL
                )

            container.setOnClickListener {
                val programInfoFragment = ProgramInfoDialogFragment.newInstance(channel, program)
                programInfoFragment.onWatchButtonClick = { channel, program, rating ->
                    val activity = binding.root.context as Activity
                    val pinChallenge = ParentalPinChallenge(rating,
                        parentalControlsViewModel,
                        activity as FragmentActivity
                    ) { context, challengeCallback -> }.setOnPassedListener {
                        activity.startActivity(PlayerActivity.getPlayerActivityIntent(activity, channel, program))
                    }
                    pinChallenge.run()
                }
                programInfoFragment.show(fragmentManager)
            }
            statusChip.show()
            btnWatch.show()
            contentSection.show()
        }

        liveChannelWidgetViewModel.programProgressLiveData.observe(this) {
            val programType = if (programDetail?.programType == "MO") "MOVIE" else "SERIES"
            binding.description.text =
                String.format("%s • %s • %d MINS LEFT", programDetail?.rating ?: "NA", programType, it.remainingTime)
        }
    }

    private fun updateWatchButtonBackground(hasFocus: Boolean) {
        binding.btnWatch.background = ContextCompat.getDrawable(
            binding.root.context,
            if (hasFocus) R.drawable.primary_button_selected else R.drawable.primary_button_unselected
        ).apply {
            if (hasFocus && this is GradientDrawable)
                themeManager.getPrimaryColor()?.let { this.setColor(it) }
        }
    }

    private fun populateError(carouselBackgroundLoader: ((String?) -> Unit)?) {
        hideAllViews()
        with(binding) {
            liveText.text = root.context.getText(R.string.error)
            liveDot.hide()
            Glide.with(root)
                .load(R.drawable.ic_warning)
                .into(channelLogo)
            programTitle.text = root.context.getString(R.string.content_unavailable)
            subtitle.text = root.context.getString(R.string.content_unavailable_message)
            statusChip.show()
            contentSection.show()
            container.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) carouselBackgroundLoader?.invoke(null)
            }
        }
    }

    private fun hideAllViews() {
        with(binding) {
            contentSection.hide()
            statusChip.hide()
            btnWatch.hide()
        }
    }

    override val focusView: View = binding.container

    fun setZoom() {
        binding.programTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        binding.subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        binding.liveText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        binding.channelLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._70dp).toInt()
        binding.channelLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._70dp).toInt()

        binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        binding.btnWatch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        binding.container.layoutParams.height = binding.root.resources.getDimension(R.dimen._330dp).toInt()
    }
}