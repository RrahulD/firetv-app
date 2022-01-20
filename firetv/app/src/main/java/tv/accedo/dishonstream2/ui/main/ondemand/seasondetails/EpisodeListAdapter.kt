package tv.accedo.dishonstream2.ui.main.ondemand.seasondetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.databinding.LayoutSeasonDetailsItemBinding
import tv.accedo.dishonstream2.domain.model.vod.EpisodeDetail
import tv.accedo.dishonstream2.domain.model.vod.ShowDetails
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class EpisodeListAdapter(
    private val episodes: List<EpisodeDetail>,
    private val baseFragment: BaseFragment,
    private val isZoomEnabled: Boolean,
    private val seasonId: Int,
    private val showDetails: ShowDetails,
    private val focusChangeListener: View.OnFocusChangeListener
) : RecyclerView.Adapter<EpisodeItemViewHolder>(), KoinComponent {
    private val themeManager: ThemeManager by inject()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EpisodeItemViewHolder(LayoutSeasonDetailsItemBinding.inflate(inflater, parent, false), baseFragment)
    }

    override fun onBindViewHolder(holder: EpisodeItemViewHolder, position: Int) {
        holder.binding.frameContainer.onFocusChangeListener = focusChangeListener
        holder.bindSeasonList(episodes[position], isZoomEnabled, themeManager, seasonId, showDetails)
        holder.binding.seasonEpisodeImage.consumeRightKeyEvent()
    }

    override fun getItemCount(): Int {
        return episodes.size
    }
}