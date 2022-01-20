package tv.accedo.dishonstream2.domain.model.home.game.stat

data class NBAGameStats(
    val home: NBAStats,
    val away: NBAStats
) : GameStats()

data class NBAStats(
    val assists: String,
    val biggestLead: String,
    val blocks: String,
    val fastBreakPts: String,
    val fieldGoalAttempted: String,
    val fieldGoalMade: String,
    val fieldGoalPercent: String,
    val flagrantFouls: String,
    val fouls: String,
    val freeThrowAttempted: String,
    val freeThrowMade: String,
    val freeThrowPct: String,
    val secondChancePoints: String,
    val points: String,
    val pointsInPaint: String,
    val pointsOffTurnover: String,
    val reboundsDefensive: String,
    val reboundsOffensive: String,
    val steals: String,
    val turnovers: String
) {
    fun toMap() =
        mapOf(
            "Second chance points" to secondChancePoints,
            "Field Goal Attempted" to fieldGoalAttempted,
            "Field Goal Made" to fieldGoalMade,
            "Field Goal Percent" to fieldGoalPercent,
            "Assists" to assists,
            "Biggest Lead" to biggestLead,
            "Blocks" to blocks,
            "Fast Break Points" to fastBreakPts,
            "Flagrant Fouls" to flagrantFouls,
            "Fouls" to fouls,
            "Free Throws Attempted" to freeThrowAttempted,
            "Free Throws Made" to freeThrowMade,
            "Free Throw Percent" to freeThrowPct,
            "Points" to points,
            "Points In Paint" to pointsInPaint,
            "Points Off Turnover" to pointsOffTurnover,
            "Rebounds Defensive" to reboundsDefensive,
            "Rebounds Offensive" to reboundsOffensive,
            "Steals" to steals,
            "Turnovers" to turnovers
        )

}