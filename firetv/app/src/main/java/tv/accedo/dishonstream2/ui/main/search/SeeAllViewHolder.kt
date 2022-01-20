package tv.accedo.dishonstream2.ui.main.search

import android.view.ViewGroup
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.LayoutSeeAllViewHolderBinding
import tv.accedo.dishonstream2.domain.model.search.SearchEpgProgramData
import tv.accedo.dishonstream2.domain.model.vod.VODContent
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.ui.main.seeall.SeeAllFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class SeeAllViewHolder(
    private val binding: LayoutSeeAllViewHolderBinding,
    private val fragmentManager: FragmentManager
) : RecyclerView.ViewHolder(binding.root) {

    fun populate(
        themeManager: ThemeManager,
        cardType: Int,
        data: List<Any>? = null,
        total: Int? = null,
        searchText: String? = null
    ) {
        themeManager.getWidgetBackgroundDrawable()?.let {
            binding.root.background = it
        }
        binding.seeAllTextView.text = String.format(
            binding.root.resources.getString(R.string.see_all_card_text),
            if (AppConstants.SeeAll.TYPE_SEARCH_VOD_CARD == cardType) data?.size else total
        )
        binding.root.consumeRightKeyEvent()
        setCard(cardType)
        itemView.setOnClickListener {
            var itemsList: List<Any>? = null
            when (cardType) {
                AppConstants.SeeAll.TYPE_SEARCH_EPG_CARD -> {
                    itemsList = data?.filterIsInstance<SearchEpgProgramData>().takeIf { it?.size == data?.size }
                }
                AppConstants.SeeAll.TYPE_SEARCH_VOD_CARD -> {
                    itemsList = data?.filterIsInstance<VODContent>().takeIf { it?.size == data?.size }
                }
            }
            itemsList?.let {
                val seeAllFragment = SeeAllFragment.newInstance(it, cardType, total, searchText)
                fragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    seeAllFragment
                ).commit()
            }
        }
    }

    private fun setCard(cardType: Any) {
        when (cardType) {
            AppConstants.SeeAll.TYPE_SEARCH_EPG_CARD -> {
                (binding.root.layoutParams as ViewGroup.LayoutParams).height =
                    binding.root.resources.getDimension(R.dimen.search_epg_item_height).toInt()
                (binding.root.layoutParams as ViewGroup.LayoutParams).width =
                    binding.root.resources.getDimension(R.dimen.search_epg_item_width).toInt()
            }
            AppConstants.SeeAll.TYPE_SEARCH_VOD_CARD -> {
                (binding.root.layoutParams as ViewGroup.LayoutParams).height =
                    binding.root.resources.getDimension(R.dimen.search_see_all_vod_item_height).toInt()
                (binding.root.layoutParams as ViewGroup.LayoutParams).width =
                    binding.root.resources.getDimension(R.dimen.search_vod_item_width).toInt()
                (binding.root.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                    binding.root.resources.getDimension(R.dimen._4dp).toInt()
                )
            }
        }
    }
}