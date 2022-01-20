package tv.accedo.dishonstream2.ui.main.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.LayoutVodViewHolderBinding
import tv.accedo.dishonstream2.domain.model.vod.Movie
import tv.accedo.dishonstream2.domain.model.vod.Show
import tv.accedo.dishonstream2.domain.model.vod.VODContent
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.ondemand.OnDemandDetailsActivity
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class VODViewHolder(
    val binding: LayoutVodViewHolderBinding,
) : RecyclerView.ViewHolder(binding.root) {
    var onSearchItemClick: ((Any) -> Unit)? = null

    fun bind(
        vodContent: VODContent, baseFragment: BaseFragment, themeManager: ThemeManager,
        isLastItem: Boolean
    ) {
        var imageUrl: String? = null
        if (vodContent is Movie) {
            imageUrl = vodContent.posterImage
        } else if (vodContent is Show) {
            imageUrl = vodContent.posterImage
        }
        if (isLastItem) {
            binding.root.consumeRightKeyEvent()
        }
        Glide.with(binding.root)
            .load(imageUrl)
            .placeholder(R.drawable.dish_image_not_found)
            .error(R.drawable.dish_image_not_found)
            .into(binding.itemImageView)

        themeManager.getSettingOptionsBackgroundDrawable()?.let { binding.root.background = it }
        itemView.setOnClickListener {
            onSearchItemClick?.invoke(vodContent)
            // open on demand details page
            OnDemandDetailsActivity.startActivity(baseFragment.requireActivity(), vodContent)
        }
    }
}