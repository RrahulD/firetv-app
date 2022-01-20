package tv.accedo.dishonstream2.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.MainFragmentBinding
import tv.accedo.dishonstream2.domain.usecase.home.Tab
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.HomeFragment
import tv.accedo.dishonstream2.ui.main.home.sports.SportsFragment
import tv.accedo.dishonstream2.ui.main.ondemand.OnDemandFragment
import tv.accedo.dishonstream2.ui.main.search.SearchFragment
import tv.accedo.dishonstream2.ui.main.settings.SettingsFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.TVGuideFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager
import tv.accedo.dishonstream2.ui.view.NavigationView

class MainFragment : BaseFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val mainViewModel: MainViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    lateinit var binding: MainFragmentBinding
    private val themeManager: ThemeManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.landingPageTabsLiveData.observe(viewLifecycleOwner) { tabs ->
            binding.navigationView.setTabs(tabs)
            selectHomeTab()
        }

        if (themeManager.usLightTheme) {
            mainViewModel.landingPageBackgroundUrlLiveData.observe(viewLifecycleOwner) { backgroundURL ->
                Glide.with(this)
                    .load(backgroundURL)
                    .into(binding.backgroundImage)
                binding.backgroundImage.show()
                binding.overlay.show()
            }

            mainViewModel.getLandingPageBackground()
        }

        mainViewModel.propertyLogoLiveData.observe(viewLifecycleOwner) {
            binding.navigationView.setLogo(it)
        }

        sharedAppViewModel.timeFormatLiveData.observe(viewLifecycleOwner) { timeFormat ->
            binding.navigationView.setTimeFormat(timeFormat)
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            binding.navigationView.setZoom(enabled)
        }

        binding.navigationView.onTabSelected = {
            onTabSelected(it)
        }
    }

    override fun onBackPressed(): Boolean {
        if (getTopFragment()?.onBackPressed() == false)
            if (binding.navigationView.currentSelectedTab() != Tab.HOME)
                selectHomeTab()
            else
                requireActivity().finish()
        return true
    }

    private fun selectHomeTab() {
        binding.navigationView.focusTab(Tab.HOME)
        onTabSelected(Tab.HOME)
    }

    fun showSportsSection() {
        binding.navigationView.focusTab(Tab.SPORTS)
        onTabSelected(Tab.SPORTS)
    }

    fun onTabSelected(tab: Tab) {
        val fragment = when (tab) {
            Tab.HOME -> HomeFragment.newInstance()
            Tab.TV_GUIDE -> TVGuideFragment.newInstance()
            Tab.SETTINGS -> SettingsFragment.newInstance()
            Tab.SPORTS -> SportsFragment.newInstance()
            Tab.SEARCH -> SearchFragment.newInstance()
            Tab.ON_DEMAND -> OnDemandFragment.newInstance()
            else -> {
                null
            }
        }
        fragment?.let {
            childFragmentManager.beginTransaction().replace(R.id.fragment_container, it).commit()
        }
    }

    fun requestFocusOnNavigation() {
        binding.navigationView.requestFocusOnNavigation()
    }

    private fun getTopFragment(): BaseFragment? {
        return childFragmentManager.fragments.filterIsInstance<BaseFragment>().firstOrNull()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val fragment = childFragmentManager.fragments.filterIsInstance<BaseFragment>().firstOrNull()
        return if (fragment?.onKeyDown(keyCode, event) == true) true
        else super.onKeyDown(keyCode, event)
    }

    fun isHeaderBarFocussed(): Boolean {
        return binding.navigationView.hasFocus()
    }

    fun getNavigationView(): NavigationView {
        return binding.navigationView
    }
}