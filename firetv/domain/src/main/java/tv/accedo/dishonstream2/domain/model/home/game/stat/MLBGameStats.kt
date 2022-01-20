package tv.accedo.dishonstream2.domain.model.home.game.stat

data class MLBGameStats(
    val home: MLBStats,
    val away: MLBStats
) : GameStats()

data class MLBStats(
    val bats: String,
    val doublePlays: String,
    val errors: String,
    val hits: String,
    val probablePitcher: String,
    val rbi: String,
    val runnersLeftOnBase: String,
    val runs: String,
    val scoringOpportunities: String,
    val scoringSuccesses: String,
    val strikeOuts: String,
    val sumBatterLeftOnBase: String,
    val totalBases: String,
    val triplePlays: String,
    val walks: String,
) {
    fun toMap() = mapOf(
        "Bats" to bats,
        "Double Plays" to doublePlays,
        "Errors" to errors,
        "Hits" to hits,
        "Probable Pitcher" to probablePitcher,
        "Rbi" to rbi,
        "Runners Left On Base" to runnersLeftOnBase,
        "Runs" to runs,
        "Scoring Opportunities" to scoringOpportunities,
        "Scoring Successes" to scoringSuccesses,
        "Strike Outs" to strikeOuts,
        "Sum Batter Left On Base" to sumBatterLeftOnBase,
        "Total Bases" to totalBases,
        "Triple Plays" to triplePlays,
        "Walks" to walks
    )
}