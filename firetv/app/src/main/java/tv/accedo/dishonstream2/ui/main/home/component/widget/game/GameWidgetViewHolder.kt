package tv.accedo.dishonstream2.ui.main.home.component.widget.game

import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import kotlinx.datetime.Instant
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.WidgetGameLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.GameStatus.*
import tv.accedo.dishonstream2.domain.model.home.template.widget.GameWidget
import tv.accedo.dishonstream2.extensions.getTimeWithDateString
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.game.GameScoreDialogFragment

@OptIn(KoinApiExtension::class)
class GameWidgetViewHolder(
    private val binding: WidgetGameLayoutBinding,
    private val fragmentManager: FragmentManager
) : WidgetViewHolder(binding.root), KoinComponent {

    private val gameWidgetViewModel: GameWidgetViewModel by inject { parametersOf(getViewHolderScope()) }

    override val focusView: View = binding.container

    fun populate(gameWidget: GameWidget) {
        hideAllViews()
        val game = gameWidget.game
        gameWidgetViewModel.timeFormatLiveData.observe(this) { timeFormat ->
            with(binding) {
                Glide.with(root)
                    .load(game.homeTeam.img)
                    .into(homeIcon)

                Glide.with(root)
                    .load(game.awayTeam.img)
                    .into(awayIcon)

                liveTag.isVisible = game.gameStatus == IN_PROGRESS

                when (game.gameStatus) {
                    IN_PROGRESS -> {
                        populateScore(game)
                    }

                    UPCOMING -> {
                        scheduleTime.text = Instant.fromEpochMilliseconds(game.scheduledDate)
                            .getTimeWithDateString(timeFormat)
                        scheduleTime.show()
                    }

                    COMPLETED -> {
                        populateScore(game)
                    }
                }

                container.setOnClickListener {
                    GameScoreDialogFragment.newInstance(game).show(fragmentManager)
                }
            }
        }
    }

    private fun populateScore(game: Game) {
        with(binding) {
            homeScore.text = game.homeScore.toString()
            awayScore.text = game.awayScore.toString()
            homeScore.show()
            awayScore.show()
        }
    }

    private fun hideAllViews() {
        with(binding) {
            homeScore.hide()
            awayScore.hide()
            scheduleTime.hide()
        }
    }

    fun setZoom(isZoomEnabled: Boolean) {
        binding.liveText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 12f else 10f)
        binding.homeScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 21f else 16f)
        binding.awayScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 21f else 16f)
        binding.scheduleTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 21f else 16f)
        binding.homeIcon.layoutParams.width =
            binding.root.resources.getDimension(if (isZoomEnabled) R.dimen._47dp else R.dimen._40dp).toInt()
        binding.homeIcon.layoutParams.height =
            binding.root.resources.getDimension(if (isZoomEnabled) R.dimen._47dp else R.dimen._40dp).toInt()
        binding.awayIcon.layoutParams.width =
            binding.root.resources.getDimension(if (isZoomEnabled) R.dimen._47dp else R.dimen._40dp).toInt()
        binding.awayIcon.layoutParams.height =
            binding.root.resources.getDimension(if (isZoomEnabled) R.dimen._47dp else R.dimen._40dp).toInt()
    }
}