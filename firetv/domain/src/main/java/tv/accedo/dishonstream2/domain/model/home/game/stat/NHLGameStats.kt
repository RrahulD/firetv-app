package tv.accedo.dishonstream2.domain.model.home.game.stat

data class NHLGameStats(
    val home: NHLStats,
    val away: NHLStats
) : GameStats()

data class NHLStats(
    val powerplayAttempts: String,
    val powerplayGoals: String,
    val shootoutAttempts: String,
    val shootoutScore: String,
    val shots: String
) {
    fun toMap() = mapOf(
        "Powerplay Attempts" to powerplayAttempts,
        "Powerplay Goals" to powerplayGoals,
        "Shootout Attempts" to shootoutAttempts,
        "Shootout Score" to shootoutScore,
        "Shots" to shots
    )
}