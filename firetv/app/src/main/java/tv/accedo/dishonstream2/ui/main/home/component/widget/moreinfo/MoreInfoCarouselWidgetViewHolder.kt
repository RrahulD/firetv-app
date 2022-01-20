package tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.CarouselWidgetMoreInfoLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.MoreInfoWidget
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.MoreInfoDialogFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class MoreInfoCarouselWidgetViewHolder(
    private val binding: CarouselWidgetMoreInfoLayoutBinding,
    private val fragmentManager: FragmentManager
) : WidgetViewHolder(binding.root), KoinComponent {

    override val focusView = binding.container

    private val themeManager: ThemeManager by inject()

    fun populate(widget: MoreInfoWidget, carouselBackgroundLoader: ((String?) -> Unit)?) {
        with(binding) {
            moreInfoTitle.text = widget.title
            moreInfoSubtitle.text = widget.subTitle
            Glide.with(root)
                .load(widget.icon.fileUrl)
                .into(moreInfoLogo)

            container.setOnClickListener { MoreInfoDialogFragment.newInstance(widget).show(fragmentManager) }
            container.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) carouselBackgroundLoader?.invoke(widget.headerImage.fileUrl)
                updateLearnMoreButtonBackground(hasFocus)
            }
            updateLearnMoreButtonBackground(container.isFocused)
            if (container.isFocused)
                carouselBackgroundLoader?.invoke(widget.headerImage.fileUrl)
            contentSection.show()
        }
    }

    private fun updateLearnMoreButtonBackground(hasFocus: Boolean) {
        binding.btnLearnMore.background = ContextCompat.getDrawable(
            binding.root.context,
            if (hasFocus) R.drawable.primary_button_selected else R.drawable.primary_button_unselected
        ).apply {
            if (hasFocus && this is GradientDrawable)
                themeManager.getPrimaryColor()?.let { this.setColor(it) }
        }
    }

    fun setZoom() {
        binding.moreInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        binding.moreInfoSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

        binding.moreInfoLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._70dp).toInt()
        binding.moreInfoLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._70dp).toInt()

        binding.btnLearnMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    }
}