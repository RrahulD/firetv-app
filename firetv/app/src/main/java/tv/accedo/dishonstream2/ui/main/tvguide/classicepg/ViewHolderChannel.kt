package tv.accedo.dishonstream2.ui.main.tvguide.classicepg

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import hu.accedo.commons.widgets.epg.EpgDataSource
import tv.accedo.dishonstream2.R

class ViewHolderChannel(view: View) : EpgDataSource.ViewHolderChannel(view) {
    val channelTextView: TextView = itemView.findViewById(R.id.text_channel)

    init {
        imageView = view.findViewById(R.id.iv_channel)


        view.setOnFocusChangeListener { _, hasFocus ->
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    if (hasFocus) R.color.brand_secondary_color else R.color.brand_primary_color
                )
            )
        }
    }
}