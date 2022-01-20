package tv.accedo.dishonstream2.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import org.koin.android.ext.android.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.ui.theme.ThemeManager

abstract class BaseFullScreenDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, this.javaClass.simpleName)
    }
}