package tv.accedo.dishonstream2.ui.main.home.dialog.game

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import kotlinx.datetime.Instant
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewGameScoreDialogBinding
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.GameStatus
import tv.accedo.dishonstream2.extensions.getTimeWithDateString
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class GameScoreDialogFragment : BaseFullScreenDialogFragment() {

    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val themeManager: ThemeManager by inject()

    companion object {
        private const val KEY_GAME = "gameId"

        fun newInstance(game: Game) = GameScoreDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_GAME, game)
            }
        }
    }

    private lateinit var binding: ViewGameScoreDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ViewGameScoreDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Game>(KEY_GAME)?.let { game ->
            sharedAppViewModel.timeFormatLiveData.observe(viewLifecycleOwner) { timeFormat ->
                with(binding) {
                    title.text = String.format("%s vs %s", game.homeTeam.name, game.awayTeam.name)
                    val dateTimeText = Instant.fromEpochMilliseconds(game.scheduledDate).getTimeWithDateString(timeFormat)
                    scheduleText.text = when (game.gameStatus) {
                        GameStatus.IN_PROGRESS -> "Period ${game.period} • ${game.venue}"
                        GameStatus.COMPLETED -> "Was Live @ $dateTimeText"
                        GameStatus.UPCOMING -> "Game Starting @ $dateTimeText\nWatch on ${game.providerCallsign}"
                    }

                    homeScore.text = when (game.gameStatus) {
                        GameStatus.UPCOMING -> "0"
                        else -> game.homeScore.toString()
                    }

                    awayScore.text = when (game.gameStatus) {
                        GameStatus.UPCOMING -> "0"
                        else -> game.awayScore.toString()
                    }

                    Glide.with(root)
                        .load(game.homeTeam.img)
                        .into(homeTeamIcon)

                    Glide.with(root)
                        .load(game.awayTeam.img)
                        .into(awayTeamIcon)

                }
            }

            sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
                if (enabled) setZoom()
            }

            val showSeeStatsButton = game.gameStatus == GameStatus.COMPLETED
            binding.seeStats.isVisible = showSeeStatsButton
            if (showSeeStatsButton)
                binding.seeStats.setOnClickListener {
                    dismiss()
                    GameStatsDialogFragment.newInstance(game).show(requireActivity().supportFragmentManager)
                }

            themeManager.getPrimaryButtonBackgroundDrawable()?.let {
                binding.seeStats.background = it
            }
        }
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
        binding.scheduleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        binding.homeScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        binding.awayScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        binding.seeStats.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

        binding.homeTeamIcon.layoutParams.height =
            resources.getDimension(R.dimen._70dp).toInt()
        binding.homeTeamIcon.layoutParams.width =
            resources.getDimension(R.dimen._70dp).toInt()

        binding.awayTeamIcon.layoutParams.height =
            resources.getDimension(R.dimen._70dp).toInt()
        binding.awayTeamIcon.layoutParams.width =
            resources.getDimension(R.dimen._70dp).toInt()
    }
}