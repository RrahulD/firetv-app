package tv.accedo.dishonstream2.ui.main.ondemand

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.databinding.ButtonOnDemandPartnersBinding
import tv.accedo.dishonstream2.domain.model.vod.VODService
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class PlayProvidersViewHolder(
    val binding: ButtonOnDemandPartnersBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        provider: VODService,
        themeManager: ThemeManager,
        isZoomEnabled: Boolean,
        clickListener: ((VODService) -> Unit)
    ) {
        binding.partnerPlayButton.text = provider.name
        binding.partnerPlayButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 19f else 15f)
        binding.partnerPlayButton.setOnClickListener {
            clickListener.invoke(provider)
        }
        themeManager.getPrimaryButtonBackgroundDrawable()?.let {
            binding.partnerPlayButton.background = it
        }
    }
}