package tv.accedo.dishonstream2.ui.main.home.dialog.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.GameStatus
import tv.accedo.dishonstream2.domain.model.home.game.League
import tv.accedo.dishonstream2.domain.model.home.game.stat.GameStats
import tv.accedo.dishonstream2.domain.usecase.home.game.GetMLBStatsUseCase
import tv.accedo.dishonstream2.domain.usecase.home.game.GetNBAStatsUseCase
import tv.accedo.dishonstream2.domain.usecase.home.game.GetNFLStatsUseCase
import tv.accedo.dishonstream2.domain.usecase.home.game.GetNHLStatsUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class GameStatsViewModel(
    private val getMLBStatsUseCase: GetMLBStatsUseCase,
    private val getNFLStatsUseCase: GetNFLStatsUseCase,
    private val getNBAStatsUseCase: GetNBAStatsUseCase,
    private val getNHLStatsUseCase: GetNHLStatsUseCase
) : BaseViewModel() {

    @OptIn(ExperimentalTime::class)
    private val gameStatusUpdateInterval = Duration.seconds(15).inWholeMilliseconds

    private val _gameStatsLiveData = MutableLiveData<GameStats>()
    val gameStatsLiveData: LiveData<GameStats> = _gameStatsLiveData

    fun getGameStatus(game: Game) {
        viewModelScope.launch {
            onLoading(true)
            try {
                val gameStats = getGameStats(game.league, game.id)
                onLoading(false)
                _gameStatsLiveData.value = gameStats
                if (game.gameStatus == GameStatus.IN_PROGRESS) {
                    scheduleGameStatusUpdaterJob(game.league, game.id)
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                onLoading(false)
                onError(ex)
            }
        }
    }

    private fun scheduleGameStatusUpdaterJob(league: League, gameId: Long) {
        viewModelScope.launch {
            while (isActive) {
                delay(gameStatusUpdateInterval)
                try {
                    val gameStats = getGameStats(league, gameId)
                    _gameStatsLiveData.value = gameStats
                } catch (ex: Exception) {
                    Timber.e("Error while updating the gamestats for $league $gameId")
                }
            }
        }
    }

    private suspend fun getGameStats(league: League, gameId: Long): GameStats {
        return when (league) {
            League.NBA -> getNBAStatsUseCase(gameId)
            League.MLB -> getMLBStatsUseCase(gameId)
            League.NFL -> getNFLStatsUseCase(gameId)
            League.NHL -> getNHLStatsUseCase(gameId)
        }
    }
}