package tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel

import android.app.Activity
import android.util.TypedValue
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.WidgetLiveChannelLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.LiveChannelWidget
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.base.resource.Resource
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.ContentErrorDialogFragment
import tv.accedo.dishonstream2.ui.main.home.dialog.programinfo.ProgramInfoDialogFragment
import tv.accedo.dishonstream2.ui.main.search.ParentalPinChallenge
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.player.PlayerActivity

@OptIn(KoinApiExtension::class)
class LiveChannelWidgetViewHolder(
    val binding: WidgetLiveChannelLayoutBinding,
    private val fragmentManager: FragmentManager,
    baseFragment: BaseFragment
) : WidgetViewHolder(binding.root, baseFragment.viewLifecycleOwner), KoinComponent {
    private val liveChannelWidgetViewModel: LiveChannelWidgetViewModel by inject { parametersOf(getViewHolderScope()) }
    private val parentalControlsViewModel: ParentalControlsViewModel by inject { parametersOf(getViewHolderScope()) }
    var onSearchItemClick: ((Any) -> Unit)? = null
    fun populate(widget: LiveChannelWidget) {
        populate(widget.channelId)
    }

    fun populateSearchData(widget: LiveChannelWidget, isLastItem: Boolean, fromSeeAll: Boolean) {
        populate(widget.channelId)
        (binding.root.layoutParams as ViewGroup.LayoutParams).height =
            binding.root.resources.getDimension(R.dimen.search_epg_item_height).toInt()
        (binding.root.layoutParams as ViewGroup.LayoutParams).width =
            binding.root.resources.getDimension(R.dimen.search_epg_item_width).toInt()
        if (fromSeeAll) {
            binding.root.setPadding(
                binding.root.resources.getDimension(R.dimen._10dp).toInt(),
                0,
                binding.root.resources.getDimension(R.dimen._10dp).toInt(),
                binding.root.resources.getDimension(R.dimen._20dp).toInt()
            )
        } else {
            binding.root.setPadding(0, 0, binding.root.resources.getDimension(R.dimen._14dp).toInt(), 0)
        }
        if (!fromSeeAll && isLastItem) {
            binding.container.consumeRightKeyEvent()
        }
    }

    override val focusView = binding.container

    private fun populate(channelId: Long) {
        liveChannelWidgetViewModel.liveChannelCurrentProgramDetails.observe(this) {
            when (it) {
                is Resource.Failure -> populateError()
                is Resource.Loading -> {
                    // TODO: show loading?
                }
                is Resource.Success -> with(it.data) {
                    populateProgramDetails(channel, program, programInfo)
                }
            }
        }

        liveChannelWidgetViewModel.programProgressLiveData.observe(this) {
            binding.programRemainingTime.text = String.format("LIVE • %d MINS LEFT", it.remainingTime)
            binding.timeBar.progress = it.completionPercent
        }

        liveChannelWidgetViewModel.loadCurrentProgramDetails(channelId)
    }

    private fun populateProgramDetails(channel: Channel, program: Program, programDetail: ProgramInfo?) {
        hideAllViews()
        with(binding) {
            programTitle.text = program.name
            liveText.text = root.context.getString(R.string.live)
            Glide.with(root)
                .load(channel.detail?.logoURL)
                .into(channelLogo)
            Glide.with(root)
                .load(programDetail?.richMediaImageInfo?.imageURL)
                .placeholder(R.drawable.dish_image_not_found)
                .error(R.drawable.dish_image_not_found)
                .into(programImage)
            container.setOnClickListener {
                onSearchItemClick?.invoke(program)
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
            liveDot.show()
            content.show()
            statusChip.show()
        }
    }

    private fun populateError() {
        hideAllViews()
        with(binding) {
            liveText.text = root.context.getText(R.string.error)
            liveDot.hide()
            Glide.with(root)
                .load(R.drawable.ic_warning)
                .into(channelLogo)
            programTitle.text = root.context.getString(R.string.content_unavailable)
            container.setOnClickListener {
                ContentErrorDialogFragment.newInstance(
                    root.context.getString(R.string.content_unavailable),
                    root.context.getString(R.string.content_unavailable_message)
                ).show(fragmentManager)
            }
            statusChip.show()
            error.show()
        }
    }

    private fun hideAllViews() {
        with(binding) {
            content.hide()
            statusChip.hide()
            error.hide()
        }
    }

    fun setZoom() {
        binding.programTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        binding.programRemainingTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        binding.channelLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._50dp).toInt()
        binding.channelLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._50dp).toInt()
        binding.liveText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        (binding.root.layoutParams as ViewGroup.MarginLayoutParams).height =
            binding.root.resources.getDimension(R.dimen._330dp).toInt()
        (binding.programTextContainer.layoutParams as ViewGroup.MarginLayoutParams).height =
            binding.root.resources.getDimension(R.dimen._90dp).toInt()
    }
}

