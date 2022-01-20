package tv.accedo.dishonstream2.ui.main.home.dialog.game

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.GameStatsListItemLayoutBinding
import tv.accedo.dishonstream2.databinding.ViewGameStatsDialogBinding
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.stat.*
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class GameStatsDialogFragment : BaseFullScreenDialogFragment() {

    companion object {
        private const val KEY_GAME = "game"
        fun newInstance(game: Game) = GameStatsDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_GAME, game)
            }
        }
    }

    private val gameStatsViewModel: GameStatsViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private lateinit var binding: ViewGameStatsDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ViewGameStatsDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Game>(KEY_GAME)?.let { game ->
            Glide.with(binding.root)
                .load(game.homeTeam.img)
                .into(binding.homeTeamIcon)

            Glide.with(binding.root)
                .load(game.awayTeam.img)
                .into(binding.awayTeamIcon)

            gameStatsViewModel.getGameStatus(game)
        }

        gameStatsViewModel.loadingLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        gameStatsViewModel.gameStatsLiveData.observe(viewLifecycleOwner) {
            populateGameStats(it)
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            if (enabled) setZoom()
        }
    }

    private fun populateGameStats(gameStats: GameStats) {
        val homeTeamStatsTextMap = when (gameStats) {
            is MLBGameStats -> gameStats.home.toMap()
            is NBAGameStats -> gameStats.home.toMap()
            is NFLGameStats -> gameStats.home.toMap()
            is NHLGameStats -> gameStats.home.toMap()
        }

        val awayTeamStatsTextMap = when (gameStats) {
            is MLBGameStats -> gameStats.away.toMap()
            is NBAGameStats -> gameStats.away.toMap()
            is NFLGameStats -> gameStats.away.toMap()
            is NHLGameStats -> gameStats.away.toMap()
        }

        val statsDescriptionList = homeTeamStatsTextMap.keys.toList()

        binding.gameStatsRecyclerView.adapter = GameStatsListAdapter(
            homeTeamStatsTextMap.values.toList(), statsDescriptionList, awayTeamStatsTextMap.values.toList(),
            sharedAppViewModel.largeFontEnabledLiveData.value ?: false
        )
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
        binding.homeTeamIcon.layoutParams.height =
            resources.getDimension(R.dimen._70dp).toInt()
        binding.homeTeamIcon.layoutParams.width =
            resources.getDimension(R.dimen._70dp).toInt()

        binding.awayTeamIcon.layoutParams.height =
            resources.getDimension(R.dimen._70dp).toInt()
        binding.awayTeamIcon.layoutParams.width =
            resources.getDimension(R.dimen._70dp).toInt()

        val params = (binding.gameStatsRecyclerView.layoutParams as ViewGroup.MarginLayoutParams)
        params.topMargin = resources.getDimensionPixelSize(R.dimen._90dp)
    }
}

class GameStatsListAdapter(
    private val homeScoreList: List<String>,
    private val gameDescriptionList: List<String>,
    private val awayScoreList: List<String>,
    private val isZoomEnabled: Boolean
) : RecyclerView.Adapter<GameStatsListAdapter.GenericGameStatsViewHolder>() {

    class GenericGameStatsViewHolder(
        private val binding: GameStatsListItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(homeScore: String, gameDescription: String, awayScore: String, isZoomEnabled: Boolean) {
            binding.homeScore.text = homeScore
            binding.gameDescription.text = gameDescription
            binding.awayScore.text = awayScore
            if (isZoomEnabled) {
                binding.homeScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                binding.gameDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                binding.awayScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericGameStatsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return GenericGameStatsViewHolder(GameStatsListItemLayoutBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: GenericGameStatsViewHolder, position: Int) {
        holder.bind(homeScoreList[position], gameDescriptionList[position], awayScoreList[position], isZoomEnabled)
    }

    override fun getItemCount(): Int = minOf(homeScoreList.size, gameDescriptionList.size, awayScoreList.size)
}