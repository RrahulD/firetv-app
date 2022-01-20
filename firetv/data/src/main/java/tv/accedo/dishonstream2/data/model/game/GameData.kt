package tv.accedo.dishonstream2.data.model.game


data class GameData(
    val id: Long,
    val awayScore: Int,
    val awayTeam: TeamData,
    val displayTime: String,
    val echoId: List<String>,
    val gameStatus: String,
    val gameType: String,
    val homeScore: Int,
    val homeTeam: TeamData,
    val league: String,
    val period: Long,
    val periodMinutes: Int,
    val periodSeconds: Int,
    val providerCallsign: String,
    val scheduledDate: String,
    val sport: String,
    val state: String,
    val tournamentId: Int,
    val venue: String
)