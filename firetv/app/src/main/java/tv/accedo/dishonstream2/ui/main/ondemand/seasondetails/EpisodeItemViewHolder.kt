package tv.accedo.dishonstream2.ui.main.ondemand.seasondetails

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.LayoutSeasonDetailsItemBinding
import tv.accedo.dishonstream2.domain.model.vod.EpisodeDetail
import tv.accedo.dishonstream2.domain.model.vod.ShowDetails
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.ondemand.OnDemandWatchDialogFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class EpisodeItemViewHolder(
    val binding: LayoutSeasonDetailsItemBinding,
    private val baseFragment: BaseFragment
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindSeasonList(
        episodeDetail: EpisodeDetail,
        isZoomEnabled: Boolean,
        themeManager: ThemeManager,
        seasonId: Int,
        showDetails: ShowDetails
    ) {
        binding.seasonEpisodeTitle.text = episodeDetail.title
        binding.seasonEpisodeTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 20f else 16f)
        binding.seasonEpisodeNumber.text =
            "$SEASON$seasonId $EPISODE${episodeDetail.episodeNo.toInt()} ${baseFragment.getString(R.string.bullet_char)} ${showDetails.classification}"
        binding.seasonEpisodeNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 18f else 15f)
        binding.seasonDescription.text = episodeDetail.overview
        binding.seasonDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 18f else 15f)
        Glide.with(binding.root)
            .load(episodeDetail.screenshotUrl)
            .placeholder(R.drawable.dish_image_not_found)
            .error(R.drawable.dish_image_not_found)
            .into(binding.seasonEpisodeImage)
        themeManager.getWidgetBackgroundDrawable()?.let {
            binding.frameContainer.background = it
        }
        binding.frameContainer.setOnFocusChangeListener { _, hasFocus ->
            binding.seasonEpisodePlayIcon.background = ContextCompat.getDrawable(
                binding.root.context,
                if (hasFocus) R.drawable.primary_button_selected else R.drawable.primary_button_unselected
            ).apply {
                if (hasFocus && this is GradientDrawable)
                    themeManager.getPrimaryColor()?.let { this.setColor(it) }
            }
        }
        binding.frameContainer.setOnClickListener {
            if (episodeDetail.vodServices.isNotEmpty()) {
                val title = "${showDetails.title} " +
                    binding.root.context.getString(R.string.on_demand_play_series_description_text)
                        .format(seasonId, episodeDetail.episodeNo.toInt())
                OnDemandWatchDialogFragment.newInstance(title, episodeDetail.vodServices).show(baseFragment.childFragmentManager)
            }
        }
    }

    companion object {
        private const val EPISODE = "E"
        private const val SEASON = "S"
    }
}