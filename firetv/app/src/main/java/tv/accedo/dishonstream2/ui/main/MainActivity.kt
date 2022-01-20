package tv.accedo.dishonstream2.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ActivityMainBinding
import tv.accedo.dishonstream2.ui.base.BaseActivity
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.splash.SplashFragment

class MainActivity : BaseActivity() {

    private var mLastKeyDownTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        addFragment(SplashFragment.newInstance())
    }

    override fun onBackPressed() {
        if (getTopFragment()?.onBackPressed() != true)
            super.onBackPressed()
    }

    fun addFragment(fragment: BaseFragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val current = System.currentTimeMillis()
        val res: Boolean
        if (current - mLastKeyDownTime < 200) {
            res = true
        } else {
            mLastKeyDownTime = current
            res = if (getTopFragment()?.onKeyDown(keyCode, event) == true)
                true
            else
                super.onKeyDown(keyCode, event)
        }
        return res
    }
}