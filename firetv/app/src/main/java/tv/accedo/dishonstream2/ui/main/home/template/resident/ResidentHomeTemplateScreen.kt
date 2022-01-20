package tv.accedo.dishonstream2.ui.main.home.template.resident

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.databinding.ResidentHomeTemplateLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.ResidentHomeTemplate
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.ResidentHomeScreenItem
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class ResidentHomeTemplateScreen(
    private val baseFragment: BaseFragment,
    private val residentHomeTemplate: ResidentHomeTemplate,
    private val isZoomEnabled: Boolean
) : KoinComponent {
    private val layoutInflater = LayoutInflater.from(baseFragment.requireContext())
    private val binding = ResidentHomeTemplateLayoutBinding.inflate(layoutInflater)
    val view = binding.root

    init {
        populateTemplate()
    }

    fun getRecyclerView(): RecyclerView {
        return binding.homescreenContentList
    }

    private fun populateTemplate() {
        with(binding) {
            val homeScreenItems = mutableListOf<ResidentHomeScreenItem>()
            homeScreenItems.add(ResidentHomeScreenItem.HeroCarousel(residentHomeTemplate.heroCarouselItems))

            residentHomeTemplate.contentContainers.forEach {
                homeScreenItems.add(ResidentHomeScreenItem.SwimLane(it))
            }

            homescreenContentList.adapter =
                ResidentHomeScreenAdapter(homeScreenItems, baseFragment, isZoomEnabled).apply {
                    carouselBackgroundLoader = { backgroundImageUrl ->
                        Glide.with(binding.root)
                            .load(backgroundImageUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(binding.backgroundImage)
                    }
                }

            homescreenContentList.setOnScrollChangeListener { _, _, _, _, _ ->
                val firstVisibleItemPos =
                    (homescreenContentList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val isVisible = firstVisibleItemPos == 0
                backgroundImage.isVisible = isVisible
                gradientBackground.isVisible = isVisible
            }
        }
    }
}