package tv.accedo.dishonstream2.ui.main.home.component.widget.error

import android.util.TypedValue
import android.view.View
import tv.accedo.dishonstream2.databinding.WidgetErrorLayoutBinding
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder

class ErrorWidgetViewHolder(
    private val binding: WidgetErrorLayoutBinding
) : WidgetViewHolder(binding.root) {
    override val focusView: View = binding.container

    fun setZoom() {
        binding.errorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
    }
}