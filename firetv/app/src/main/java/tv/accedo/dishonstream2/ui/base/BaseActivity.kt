package tv.accedo.dishonstream2.ui.base

import androidx.fragment.app.FragmentActivity

/* Base Activity for OnStream
Extend all the activities using this activity.*/
open class BaseActivity : FragmentActivity() {

    fun getTopFragment(): BaseFragment? {
        return supportFragmentManager.fragments.filterIsInstance<BaseFragment>().getOrNull(0)
    }
}