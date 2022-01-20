package tv.accedo.dishonstream2.ui.main.tvguide.classicepg

import android.view.View
import hu.accedo.commons.widgets.epg.EpgDataSource
import tv.accedo.dishonstream2.R

class ViewHolderHairline(view: View) : EpgDataSource.ViewHolderHairline(view) {
    init {
        viewLine = view.findViewById(R.id.viewLine)
    }
}