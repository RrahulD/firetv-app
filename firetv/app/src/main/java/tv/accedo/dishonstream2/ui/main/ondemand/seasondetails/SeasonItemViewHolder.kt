package tv.accedo.dishonstream2.ui.main.ondemand.seasondetails

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.databinding.LayoutSeasonsListItemBinding
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class SeasonItemViewHolder(
    val binding: LayoutSeasonsListItemBinding,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindSeasonList(
        seasonName: Int,
        isZoomEnabled: Boolean,
        themeManager: ThemeManager
    ) {
        binding.seasonButton.text = "$SEASON_TEXT $seasonName"
        binding.seasonButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 18f else 15f)
        themeManager.getPrimaryButtonBackgroundDrawable()?.let { binding.seasonButton.background = it }
    }

    companion object {
        private const val SEASON_TEXT = "SEASON "
    }
}