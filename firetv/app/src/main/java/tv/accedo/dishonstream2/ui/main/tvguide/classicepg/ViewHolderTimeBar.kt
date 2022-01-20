package tv.accedo.dishonstream2.ui.main.tvguide.classicepg

import android.util.TypedValue
import android.view.View
import hu.accedo.commons.widgets.epg.EpgDataSource
import tv.accedo.dishonstream2.R

class ViewHolderTimeBar(view: View, isZoomed: Boolean) : EpgDataSource.ViewHolderTimebar(view) {
    init {
        textView = view.findViewById(R.id.tv_time)
        if (isZoomed) textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
    }
}