package tv.accedo.dishonstream2.ui.base

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import tv.accedo.dishonstream2.ui.dialog.GenericErrorDialogFragment

open class BaseFragment : Fragment() {

    fun showFatalErrorDialog(title: String, message: String) {
        Firebase.crashlytics.log("$title => $message")
        showErrorDialog(title, message) { requireActivity().finish() }
    }

    private fun showErrorDialog(title: String, message: String, onCtaClick: (() -> Unit)? = null) {
        val errorDialog = GenericErrorDialogFragment.newInstance(title, message)
            .apply { this.onCtaClick = onCtaClick }
        errorDialog.show(requireActivity().supportFragmentManager)
    }

    /**
     * Other fragments can override this method to handle the hardware back press
     * @return true if the back press is handled.
     */
    open fun onBackPressed(): Boolean = false

    /**
     * Other fragments can override this method to handle Key down event
     * @return true if the event is handled. returning false will propagate the event further
     */
    open fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean = false
}