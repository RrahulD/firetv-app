package tv.accedo.dishonstream2.ui.main.tvguide.classicepg

import android.view.View
import hu.accedo.commons.widgets.epg.EpgDataSource
import tv.accedo.dishonstream2.R

class ViewHolderPlaceHolder(view: View) : EpgDataSource.ViewHolderPlaceholder(view) {
    init {
        textView = view.findViewById(R.id.textView)
    }
}