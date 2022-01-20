package tv.accedo.dishonstream2.ui.main.home.component.widget.staticad

import android.util.TypedValue
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.WidgetStaticAdLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.StaticAdWidget
import tv.accedo.dishonstream2.extensions.addToFocusHierarchy
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.removeFromFocusHierarchy
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.AdvertInfoDialogFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class StaticAdWidgetViewHolder(
    private val binding: WidgetStaticAdLayoutBinding,
    private val fragmentManager: FragmentManager
) : WidgetViewHolder(binding.root), KoinComponent {
    val view = binding.root

    private val themeManager: ThemeManager by inject()
    override val focusView = binding.container

    fun populate(widget: StaticAdWidget, isLargeSwimLane: Boolean) {
        with(binding) {
            if (isLargeSwimLane) {
                advertTitle.text = widget.title
                advertSubtitle.text = widget.subTitle
                Glide.with(root)
                    .load(widget.icon.fileUrl)
                    .into(advertLogo)
                smallWidgetFocusView.removeFromFocusHierarchy()
                smallWidgetFocusView.hide()
                container.addToFocusHierarchy()
                container.setOnClickListener { showAdvertDialog(widget) }
                advertLargeViews.show()
            } else {
                container.removeFromFocusHierarchy()
                smallWidgetFocusView.show()
                smallWidgetFocusView.addToFocusHierarchy()
                smallWidgetFocusView.setOnClickListener { showAdvertDialog(widget) }
                advertLargeViews.hide()
                themeManager.getStaticAdRoundedBackgroundDrawable()?.let { smallWidgetFocusView.background = it }
            }

            Glide.with(root)
                .load(widget.adImage.fileUrl)
                .into(advertPoster)
        }
    }

    private fun showAdvertDialog(widget: StaticAdWidget) {
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

    fun setZoom() {
        binding.advertTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        binding.advertSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

        binding.advertLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._50dp).toInt()
        binding.advertLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._50dp).toInt()

        binding.advertSubtitle.maxLines = 1

        binding.advertTextLogo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

        (binding.advertTextContainer.layoutParams as ViewGroup.MarginLayoutParams).height =
            binding.root.resources.getDimension(R.dimen._95dp).toInt()

    }
}