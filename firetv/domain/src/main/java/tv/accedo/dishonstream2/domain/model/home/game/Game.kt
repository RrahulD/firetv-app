package tv.accedo.dishonstream2.domain.model.home.game

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(
    val id: Long,
    val homeScore: Int,
    val awayScore: Int,
    val awayTeam: Team,
    val displayTime: String,
    val gameStatus: GameStatus,
    val homeTeam: Team,
    val league: League,
    val providerCallsign: String,
    val scheduledDate: Long,
    val period: Long,
    val venue: String
) : Parcelable

enum class GameStatus(val value: String) {
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    UPCOMING("UPCOMING")
}

enum class League(val value: String) {
    NBA("nba"),
    MLB("mlb"),
    NFL("nfl"),
    NHL("nhl")
}

@Parcelize
data class Team(
    val alias: String,
    val city: String,
    val img: String?,
    val name: String,
) : Parcelable

