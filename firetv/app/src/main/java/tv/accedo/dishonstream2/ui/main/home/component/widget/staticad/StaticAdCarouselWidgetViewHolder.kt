package tv.accedo.dishonstream2.ui.main.home.component.widget.staticad

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.CarouselWidgetStaticAdLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.StaticAdWidget
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.AdvertInfoDialogFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class StaticAdCarouselWidgetViewHolder(
    private val binding: CarouselWidgetStaticAdLayoutBinding,
    private val fragmentManager: FragmentManager
) : WidgetViewHolder(binding.root), KoinComponent {

    override val focusView = binding.container

    private val themeManager: ThemeManager by inject()

    fun populate(widget: StaticAdWidget, carouselBackgroundLoader: ((String?) -> Unit)?) {
        with(binding) {
            adTitle.text = widget.title
            adSubtitle.text = widget.subTitle
            Glide.with(root)
                .load(widget.icon.fileUrl)
                .into(adLogo)

            container.setOnClickListener {
                AdvertInfoDialogFragment.newInstance(
                    AdvertInfoDialogFragment.AdvertDialogInfo(
                        title = widget.title,
                        subTitle = widget.subTitle,
                        description = widget.description,
                        imgUrl = widget.adImage.fileUrl,
                        link = widget.link
                    )
                ).show(fragmentManager)
            }
            container.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) carouselBackgroundLoader?.invoke(widget.adImage.fileUrl)
                updateLearnMoreButtonBackground(hasFocus)
            }
            updateLearnMoreButtonBackground(container.isFocused)
            if (container.isFocused)
                carouselBackgroundLoader?.invoke(widget.adImage.fileUrl)
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
        binding.adTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        binding.adSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

        binding.adLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._70dp).toInt()
        binding.adLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._70dp).toInt()

        binding.btnLearnMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    }
}