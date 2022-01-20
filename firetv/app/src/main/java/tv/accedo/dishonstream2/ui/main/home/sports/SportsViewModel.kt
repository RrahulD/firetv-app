package tv.accedo.dishonstream2.ui.main.home.sports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.League
import tv.accedo.dishonstream2.domain.usecase.home.game.GetCurrentGamesUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class SportsViewModel(
    private val getCurrentGamesUseCase: GetCurrentGamesUseCase
) : BaseViewModel() {

    @OptIn(ExperimentalTime::class)
    private val gamesRefreshTime = Duration.minutes(15).inWholeMilliseconds

    private val _leagueGamesLiveData = MutableLiveData<Map<League, List<Game>>>()
    val leagueGamesLiveData: LiveData<Map<League, List<Game>>> = _leagueGamesLiveData

    init {
        getCurrentGames()
    }

    private fun getCurrentGames() {
        viewModelScope.launch {
            onLoading(true)
            try {
                val leagueGames = getCurrentGamesUseCase()
                onLoading(false)
                _leagueGamesLiveData.value = leagueGames
                scheduleGamesRefreshTask()
            } catch (ex: Exception) {
                onLoading(false)
                onError(ex)
            }
        }
    }

    private fun scheduleGamesRefreshTask() {
        viewModelScope.launch {
            while (isActive) {
                delay(gamesRefreshTime)
                try {
                    val leagueGames = getCurrentGamesUseCase()
                    _leagueGamesLiveData.value = leagueGames
                } catch (ex: Exception) {
                    Timber.e("Error while refreshing the current games")
                }
            }
        }
    }
}