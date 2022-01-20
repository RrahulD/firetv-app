package tv.accedo.dishonstream2.ui.main.home.component.widget.sports

import android.util.TypedValue
import android.view.View
import tv.accedo.dishonstream2.databinding.WidgetSportsLayoutBinding
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.MainFragment
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder

class SportsWidgetViewHolder(
    private val binding: WidgetSportsLayoutBinding,
    private val baseFragment: BaseFragment
) : WidgetViewHolder(binding.root) {

    override val focusView: View = binding.container

    init {
        binding.container.setOnClickListener {
            val parentFragment = baseFragment.parentFragment
            if (parentFragment is MainFragment)
                parentFragment.showSportsSection()
        }
    }

    fun setZoom() {
        binding.sportsHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
    }
}