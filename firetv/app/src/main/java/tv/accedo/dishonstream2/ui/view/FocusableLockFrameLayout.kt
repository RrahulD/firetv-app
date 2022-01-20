package tv.accedo.dishonstream2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class FocusableLockFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    override fun clearFocus() {
        this.parent?.let {
            super.clearFocus()
        }
    }
}