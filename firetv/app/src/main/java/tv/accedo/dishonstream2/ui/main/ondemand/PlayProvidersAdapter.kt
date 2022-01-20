package tv.accedo.dishonstream2.ui.main.ondemand

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.databinding.ButtonOnDemandPartnersBinding
import tv.accedo.dishonstream2.domain.model.vod.VODService
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class PlayProvidersAdapter(
    private val providers: List<VODService>,
    private val isZoomEnabled: Boolean,
    private val focusChangeListener: View.OnFocusChangeListener,
    private val clickListener: ((VODService) -> Unit)
) : RecyclerView.Adapter<PlayProvidersViewHolder>(), KoinComponent {
    private val themeManager: ThemeManager by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayProvidersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlayProvidersViewHolder(ButtonOnDemandPartnersBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: PlayProvidersViewHolder, position: Int) {
        holder.binding.partnerPlayButton.onFocusChangeListener = focusChangeListener
        if (position == 0) {
            holder.binding.partnerPlayButton.nextFocusUpId = holder.binding.partnerPlayButton.id
        } else if (position == providers.size - 1) {
            holder.binding.partnerPlayButton.nextFocusDownId = holder.binding.partnerPlayButton.id
        }
        holder.bind(providers[position], themeManager, isZoomEnabled, clickListener)
    }

    override fun getItemCount(): Int {
        return providers.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}