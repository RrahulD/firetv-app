package tv.accedo.dishonstream2.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewNavigationViewBinding
import tv.accedo.dishonstream2.domain.usecase.home.Tab
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.theme.ThemeManager


@OptIn(KoinApiExtension::class)
class NavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnFocusChangeListener, KoinComponent {

    private val themeManager: ThemeManager by inject()

    val binding: ViewNavigationViewBinding =
        ViewNavigationViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var currentSelectedTab = Tab.HOME
    private var currentFocusedTab = Tab.HOME
    var onTabSelected: ((Tab) -> Unit)? = null
    private var isZoomEnabled = false

    init {
        binding.home.onFocusChangeListener = this
        binding.tvGuide.onFocusChangeListener = this
        binding.onDemand.onFocusChangeListener = this
        binding.recordings.onFocusChangeListener = this
        binding.search.onFocusChangeListener = this
        binding.settings.onFocusChangeListener = this

        binding.home.setOnClickListener { selectTab(Tab.HOME) }
        binding.tvGuide.setOnClickListener { selectTab(Tab.TV_GUIDE) }
        binding.settings.setOnClickListener { selectTab(Tab.SETTINGS) }
        binding.search.setOnClickListener { selectTab(Tab.SEARCH) }
        binding.onDemand.setOnClickListener { selectTab(Tab.ON_DEMAND) }

        updateBorderColorBasedOnTheme()
    }

    private fun updateBorderColorBasedOnTheme() {
        themeManager.getPrimaryColor()?.let {
            with(binding) {
                homeBorder.background = ColorDrawable(it)
                tvGuideBorder.background = ColorDrawable(it)
                settingsBorder.background = ColorDrawable(it)
                onDemandBorder.background = ColorDrawable(it)
                recordingsBorder.background = ColorDrawable(it)
                searchBorder.background = ColorDrawable(it)
            }
        }
    }

    private fun selectTab(tab: Tab) {
        if (currentSelectedTab == tab) return
        getTextViewForTab(currentSelectedTab)?.typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
        getTextViewForTab(tab)?.typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
        currentSelectedTab = tab
        onTabSelected?.invoke(tab)
    }

    fun unSelectLastTab() {
        getTextViewForTab(currentSelectedTab)?.typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
        currentSelectedTab = Tab.NONE
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        val tabForView = when (v.id) {
            R.id.home -> Tab.HOME
            R.id.tv_guide -> Tab.TV_GUIDE
            R.id.settings -> Tab.SETTINGS
            R.id.search -> Tab.SEARCH
            R.id.on_demand -> Tab.ON_DEMAND
            else -> Tab.HOME
        }

        updateTabStyleBasedOnFocus(tabForView, hasFocus)
    }

    fun currentSelectedTab() = currentSelectedTab

    private fun updateTabStyleBasedOnFocus(tab: Tab, hasFocus: Boolean) {
        val typeFace = ResourcesCompat.getFont(
            context,
            if (hasFocus || tab == currentSelectedTab) R.font.roboto_bold else R.font.roboto_regular
        )
        hideCurrentFocusedTabBorder()
        getTextViewForTab(tab)?.typeface = typeFace
        if (hasFocus) {
            animateSelectedTab(tab)
            currentFocusedTab = tab
        }
    }

    private fun hideCurrentFocusedTabBorder() {
        val borderView = getBorderViewForTab(currentFocusedTab)
        borderView?.layoutParams = borderView?.layoutParams?.apply { width = 0 }
        borderView?.hide()
    }

    private fun getBorderViewForTab(tab: Tab): View? = when (tab) {
        Tab.HOME, Tab.SPORTS -> binding.homeBorder
        Tab.TV_GUIDE -> binding.tvGuideBorder
        Tab.SETTINGS -> binding.settingsBorder
        Tab.SEARCH -> binding.searchBorder
        Tab.ON_DEMAND -> binding.onDemandBorder
        else -> {
            null
        }
    }

    private fun getTextViewForTab(tab: Tab): TextView? = when (tab) {
        Tab.HOME, Tab.SPORTS -> binding.home
        Tab.TV_GUIDE -> binding.tvGuide
        Tab.SETTINGS -> binding.settings
        Tab.SEARCH -> binding.search
        Tab.ON_DEMAND -> binding.onDemand
        else -> {
            null
        }
    }

    fun setLogo(logoURL: String) {
        Glide.with(this)
            .load(logoURL)
            .into(binding.logo)
    }

    fun setTransparency(value: Boolean) {
        if (value) binding.root.setBackgroundColor(Color.TRANSPARENT) else binding.root.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.brand_primary_color
            )
        )
    }

    fun setTabs(tabs: List<Tab>) {
        for (tab in tabs) {
            when (tab) {
                Tab.HOME -> binding.home.show()
                Tab.TV_GUIDE -> binding.tvGuide.show()
                Tab.SETTINGS -> binding.settings.show()
                Tab.SEARCH -> binding.search.show()
                Tab.ON_DEMAND -> binding.onDemand.show()
            }
        }
        selectTab(Tab.HOME)
    }

    private fun animateSelectedTab(tab: Tab) {
        val borderView = getBorderViewForTab(tab)
        borderSizes[tab]?.let {
            val animator = ValueAnimator.ofInt(0, it)
            animator.apply {
                duration = 500
                addUpdateListener { valueAnimator ->
                    val width = valueAnimator.animatedValue as Int + (if (isZoomEnabled) ZOOMED_EXTRA_BORDER_WIDTH else 0)
                    borderView?.layoutParams = borderView?.layoutParams?.apply { this.width = width }
                }
                start()
            }
        }
        borderView?.show()
    }

    fun setZoom(enabled: Boolean) {
        isZoomEnabled = enabled
        if (enabled) {
            binding.home.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            binding.tvGuide.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            binding.settings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            binding.search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            binding.recordings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            binding.onDemand.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            binding.tvGuide.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
        } else {
            binding.home.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            binding.tvGuide.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            binding.settings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            binding.search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            binding.recordings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            binding.onDemand.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            binding.tvGuide.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        }
    }

    fun focusTab(tab: Tab) {
        requestFocusOnTab(tab)
        selectTab(tab)
    }

    fun requestFocusOnNavigation() {
        focusTab(currentSelectedTab)
    }

    fun requestFocusOnTab(tab: Tab) {
        getTextViewForTab(tab)?.requestFocus()
    }

    fun setTimeFormat(timeFormat: TimeFormat) {
        if (timeFormat == TimeFormat.FORMAT_12_HOUR) {
            binding.time.format12Hour = "hh:mm a"
            binding.time.format24Hour = null
        } else {
            binding.time.format24Hour = "HH:mm"
            binding.time.format12Hour = null
        }
    }

    companion object {
        val borderSizes = mapOf<Tab, Int>(
            Tab.HOME to 120,
            Tab.TV_GUIDE to 180,
            Tab.SEARCH to 150,
            Tab.ON_DEMAND to 235,
            Tab.SETTINGS to 170
        )
        const val ZOOMED_EXTRA_BORDER_WIDTH = 30
    }
}