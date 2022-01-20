package tv.accedo.dishonstream2.ui.main.ondemand.seasondetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.databinding.LayoutSeasonsListItemBinding

import tv.accedo.dishonstream2.extensions.consumeLeftKeyEvent
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class SeasonListAdapter(
    private val seasons: List<Int>,
    private val isZoomEnabled: Boolean,
    private val focusChangeListener: View.OnFocusChangeListener,
    private val seasonClickCallback: ((Int, Int) -> Unit)
) : RecyclerView.Adapter<SeasonItemViewHolder>(), KoinComponent {
    private val themeManager: ThemeManager by inject()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SeasonItemViewHolder(LayoutSeasonsListItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SeasonItemViewHolder, position: Int) {
        holder.binding.seasonButton.onFocusChangeListener = focusChangeListener
        holder.bindSeasonList(seasons[position], isZoomEnabled, themeManager)
        holder.binding.seasonButton.consumeLeftKeyEvent()
        holder.binding.seasonButton.setOnClickListener {
            seasonClickCallback.invoke(seasons[position], position)
        }
    }

    override fun getItemCount(): Int {
        return seasons.size
    }

}