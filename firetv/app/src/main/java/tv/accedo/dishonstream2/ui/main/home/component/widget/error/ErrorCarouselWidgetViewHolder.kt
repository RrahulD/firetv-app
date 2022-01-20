package tv.accedo.dishonstream2.ui.main.home.component.widget.error

import android.util.TypedValue
import android.view.View
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.CarouselWidgetErrorLayoutBinding
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder

@OptIn(KoinApiExtension::class)
class ErrorCarouselWidgetViewHolder(
    private val binding: CarouselWidgetErrorLayoutBinding
) : WidgetViewHolder(binding.root), KoinComponent {

    override val focusView: View = binding.container

    fun setZoom() {
        binding.errorChip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        binding.programTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        binding.subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)
        binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        binding.channelLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._70dp).toInt()
        binding.channelLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._70dp).toInt()
    }
}