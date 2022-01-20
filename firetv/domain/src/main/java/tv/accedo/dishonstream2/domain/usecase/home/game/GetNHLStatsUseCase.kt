package tv.accedo.dishonstream2.domain.usecase.home.game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.model.home.game.League
import tv.accedo.dishonstream2.domain.model.home.game.stat.GameStats
import tv.accedo.dishonstream2.domain.repository.DishRepository

class GetNHLStatsUseCase(
    private val dishRepository: DishRepository
) {
    suspend operator fun invoke(gameId: Long): GameStats = withContext(Dispatchers.IO) {
        dishRepository.getGameStats(gameId, League.NHL)
    }
}