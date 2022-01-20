package tv.accedo.dishonstream2.ui.main.settings.base

import android.os.Bundle
import android.view.View
import org.koin.android.ext.android.inject
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.settings.SettingsFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

abstract class BaseSubSettingFragment : BaseFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireSettingFragment().settingsViewModel.lastFocussedViewId?.let {
            view.findViewById<View>(it)?.requestFocus()
        }
    }

    protected val themeManager: ThemeManager by inject()

    protected fun requireSettingFragment(): SettingsFragment {
        if (parentFragment is SettingsFragment)
            return parentFragment as SettingsFragment
        throw RuntimeException("Subclass of BaseSettingSubFragment fragment should only be attached to childfragment manager of SettingsFragment")
    }

    protected fun replaceFragment(fragment: BaseSubSettingFragment) {
        requireSettingFragment().replaceFragment(fragment)
    }

    protected fun setLastFocussedViewId(id: Int) {
        requireSettingFragment().settingsViewModel.lastFocussedViewId = id
    }
}