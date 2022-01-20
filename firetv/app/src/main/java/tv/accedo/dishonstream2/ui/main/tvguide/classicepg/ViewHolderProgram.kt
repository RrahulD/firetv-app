package tv.accedo.dishonstream2.ui.main.tvguide.classicepg

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import hu.accedo.commons.widgets.epg.EpgDataSource
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.ui.view.FocusableLockFrameLayout

class ViewHolderProgram(view: View, isZoom: Boolean) : EpgDataSource.ViewHolderProgram(view) {

    val playIcon: ImageView = view.findViewById(R.id.play_icon)
    val focusableView: FocusableLockFrameLayout = view.findViewById(R.id.parentLayout)
    var tvProgramNumber: TextView? = null

    init {
        if (!isZoom) {
            tvProgramNumber = view.findViewById(R.id.tv_episode_number)
        }
        textView = view.findViewById(R.id.tv_program)
    }

}