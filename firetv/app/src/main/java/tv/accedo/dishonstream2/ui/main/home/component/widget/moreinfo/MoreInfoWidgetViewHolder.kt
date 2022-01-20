package tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo

import android.app.Activity
import android.util.TypedValue
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.WidgetMoreInfoLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.MoreInfoWidget
import tv.accedo.dishonstream2.domain.model.search.SearchEpgProgramData
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.MoreInfoDialogFragment
import tv.accedo.dishonstream2.ui.main.home.dialog.programinfo.ProgramInfoDialogFragment
import tv.accedo.dishonstream2.ui.main.search.ParentalPinChallenge
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.player.PlayerActivity

@OptIn(KoinApiExtension::class)
class MoreInfoWidgetViewHolder(
    val binding: WidgetMoreInfoLayoutBinding,
    private val fragmentManager: FragmentManager
) : WidgetViewHolder(binding.root), KoinComponent {
    private val viewModel: MoreInfoWidgetViewModel by inject { parametersOf(getViewHolderScope()) }
    private val parentalControlsViewModel: ParentalControlsViewModel by inject { parametersOf(getViewHolderScope()) }
    override val focusView = binding.container

    fun populate(widget: MoreInfoWidget) {
        with(binding) {
            moreInfoTitle.text = widget.title
            moreInfoDescription.text = widget.subTitle
            Glide.with(root)
                .load(widget.headerImage.fileUrl)
                .placeholder(R.drawable.dish_image_not_found)
                .error(R.drawable.dish_image_not_found)
                .into(moreInfoPoster)
            Glide.with(root)
                .load(widget.icon.fileUrl)
                .into(moreInfoLogo)

            container.setOnClickListener {
                MoreInfoDialogFragment.newInstance(widget).show(fragmentManager)
            }
            content.show()
        }
    }

    fun populate(
        data: SearchEpgProgramData,
        onSearchItemClick: ((Any) -> Unit)? = null,
        isLastItem: Boolean, fromSeeAll: Boolean
    ) {
        with(binding) {
            if (!fromSeeAll && isLastItem) {
                container.consumeRightKeyEvent()
            }
            (root.layoutParams as ViewGroup.LayoutParams).height =
                root.resources.getDimension(R.dimen.search_epg_item_height).toInt()
            (root.layoutParams as ViewGroup.LayoutParams).width =
                root.resources.getDimension(R.dimen.search_epg_item_width).toInt()
            if (fromSeeAll) {
                root.setPadding(
                    root.resources.getDimension(R.dimen._10dp).toInt(),
                    0,
                    root.resources.getDimension(R.dimen._10dp).toInt(),
                    root.resources.getDimension(R.dimen._20dp).toInt()
                )
            } else {
                root.setPadding(0, 0, root.resources.getDimension(R.dimen._14dp).toInt(), 0)
            }
            moreInfoTitle.text = data.program.name
            moreInfoDescription.text = data.programInfo?.description
            Glide.with(root)
                .load(data.programInfo?.richMediaImageInfo?.imageURL)
                .placeholder(R.drawable.dish_image_not_found)
                .error(R.drawable.dish_image_not_found)
                .into(moreInfoPoster)
            Glide.with(root)
                .load(data.program.channelLogoUrl)
                .into(moreInfoLogo)

            container.setOnClickListener {
                onSearchItemClick?.invoke(data.program)
                getViewHolderScope().launch(Dispatchers.Main) {
                    val channel: Channel? = viewModel.getChannel(data.program.channelId)
                    channel?.let {
                        val programInfoFragment = ProgramInfoDialogFragment.newInstance(it, data.program)
                        programInfoFragment.onWatchButtonClick = { channel, program, rating ->
                            val activity = binding.root.context as Activity
                            val pinChallenge = ParentalPinChallenge(
                                rating,
                                parentalControlsViewModel,
                                activity as FragmentActivity
                            ) { context, challengeCallback -> }.setOnPassedListener {
                                activity.startActivity(PlayerActivity.getPlayerActivityIntent(activity, channel, program))
                            }
                            pinChallenge.run()
                        }
                        programInfoFragment.show(fragmentManager)
                    }
                }
            }
            content.show()
        }
    }

    fun setZoom() {
        binding.moreInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        binding.moreInfoDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

        binding.moreInfoLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._50dp).toInt()
        binding.moreInfoLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._50dp).toInt()

        binding.errorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        binding.moreInfoDescription.maxLines = 1

        (binding.root.layoutParams as ViewGroup.MarginLayoutParams).height =
            binding.root.resources.getDimension(R.dimen._500dp).toInt()
        (binding.moreInfoTextContainer.layoutParams as ViewGroup.MarginLayoutParams).height =
            binding.root.resources.getDimension(R.dimen._95dp).toInt()
    }
}