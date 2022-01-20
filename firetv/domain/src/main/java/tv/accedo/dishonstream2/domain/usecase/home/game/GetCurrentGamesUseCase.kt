package tv.accedo.dishonstream2.domain.usecase.home.game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.League
import tv.accedo.dishonstream2.domain.repository.DishRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class GetCurrentGamesUseCase(
    private val dishRepository: DishRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(): Map<League, List<Game>> = withContext(Dispatchers.IO) {
        val formatter = SimpleDateFormat("yyyy-d-M ZZ", Locale.getDefault())
        val now = Clock.System.now()
        val startDate =
            normalizeDateString(formatter.format((now - Duration.days(1)).toEpochMilliseconds()))
        val endDate =
            normalizeDateString(formatter.format((now + Duration.days(2)).toEpochMilliseconds()))
        val games = dishRepository.getCurrentGames(startDate, endDate)
        games.groupBy { it.league }
    }

    // if the timezone is in positive values, the gamefinder API throws error,
    // so in case of positive timezones, we normalize if to UTC
    private fun normalizeDateString(dateString: String): String {
        if (dateString.contains("+")) {
            val timeZone = dateString.substring(dateString.length - 5)
            return dateString.replace(timeZone, "+0000")
        }
        return dateString
    }
}