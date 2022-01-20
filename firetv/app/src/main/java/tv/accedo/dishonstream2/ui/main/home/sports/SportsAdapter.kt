package tv.accedo.dishonstream2.ui.main.home.sports

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.SportsScreenLeagueLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.League
import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneType
import tv.accedo.dishonstream2.domain.model.home.template.widget.GameWidget
import tv.accedo.dishonstream2.domain.model.home.template.widget.Widget
import tv.accedo.dishonstream2.extensions.capitalise
import tv.accedo.dishonstream2.extensions.consumeLeftKeyEvent
import tv.accedo.dishonstream2.extensions.consumeRightKeyEvent
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.component.swimlane.SwimLane
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderType
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.util.WidgetUIHelper
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class SportsAdapter(
    private val widgetViewHolderFactory: WidgetViewHolderFactory,
    private val widgetUIHelper: WidgetUIHelper,
    private val baseFragment: BaseFragment,
    private val leagueGames: Map<League, List<Game>>,
    private val isZoomEnabled: Boolean
) : RecyclerView.Adapter<SportsAdapter.SportsViewHolder>(), KoinComponent {

    private val themeManager: ThemeManager by inject()
    private val leagues = leagueGames.keys.toList()

    inner class SportsViewHolder(
        private val binding: SportsScreenLeagueLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(parentPosition: Int, league: League) {
            val games = leagueGames[league]?.sortedBy { it.scheduledDate }?.sortedBy { it.gameStatus }
            binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 23f else 18f)
            binding.title.text = league.value.capitalise()
            binding.swimLane.setDataSource(object : SwimLane.DataSource() {
                override fun type(): SwimLaneType = SwimLaneType.NORMAL

                override fun getWidgets(): List<Widget> = games?.map { GameWidget(it) } ?: emptyList()

                override fun onCreateWidgetViewHolder(parent: ViewGroup, widget: Widget): WidgetViewHolder {
                    return widgetViewHolderFactory.getWidgetViewHolder(
                        widget,
                        baseFragment,
                        parent,
                        WidgetViewHolderType.STANDARD, isZoomEnabled
                    )
                }

                override fun onBindWidgetViewHolder(
                    widgetViewHolder: WidgetViewHolder,
                    widget: Widget,
                    position: Int,
                    itemsCount: Int,
                    isLargeSwimLane: Boolean
                ) {
                    widgetUIHelper.populateWidget(widget, widgetViewHolder, isLargeSwimLane, isZoomEnabled)
                    if (parentPosition == 0) widgetViewHolder.focusView.nextFocusUpId = R.id.home
                    if (position == 0) widgetViewHolder.focusView.consumeLeftKeyEvent()
                    if (position == itemsCount - 1) widgetViewHolder.focusView.consumeRightKeyEvent()
                    themeManager.getWidgetBackgroundDrawable()?.let { widgetViewHolder.focusView.background = it }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SportsViewHolder(SportsScreenLeagueLayoutBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
        holder.bind(position, leagues[position])
    }

    override fun getItemCount(): Int = leagues.size
}