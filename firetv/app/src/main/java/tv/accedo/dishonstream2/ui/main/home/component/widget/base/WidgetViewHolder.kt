package tv.accedo.dishonstream2.ui.main.home.component.widget.base

import android.view.View
import androidx.lifecycle.LifecycleOwner
import tv.accedo.dishonstream2.ui.base.viewholder.BaseLifeCycleViewHolder

abstract class WidgetViewHolder(
    view: View,
    parentLifecycleOwner: LifecycleOwner? = null
) : BaseLifeCycleViewHolder(view, parentLifecycleOwner) {
    abstract val focusView: View
}