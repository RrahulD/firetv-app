package tv.accedo.dishonstream2.domain.model.home.game.stat

import kotlin.math.ceil

data class NFLGameStats(
    val home: NFLStats,
    val away: NFLStats
) : GameStats()

data class NFLStats(
    val firstDownsNumber: String,
    val thirdDownEfficiencyPercent: String,
    val fourthDownEfficiencyPercent: String,
    val gameTotalsPlays: String,
    val gameTotalsNetYards: String,
    val interceptionReturnAttempts: String,
    val passingNetYards: String,
    val passingSacked: String,
    val totalPenalties: String,
    val penaltiesYards: String,
    val puntingPunts: String,
    val rushingYards: String,
    val fumbleLost: String,
    val timeOfPossessionMinutes: String,
    val timeOfPossessionSeconds: String
) {
    fun toMap() = mapOf(
        "Total Yards" to (passingNetYards.toLong() + rushingYards.toInt()).toString(),
        "Passing Yards" to passingNetYards,
        "Rushing Yards" to rushingYards,
        "Yards Per Play" to (ceil((gameTotalsNetYards.toDouble() / gameTotalsPlays.toDouble()) * 10) / 10).toString(),
        "First Downs" to firstDownsNumber,
        "Third Down Efficiency" to thirdDownEfficiencyPercent,
        "Fourth Down Efficiency" to fourthDownEfficiencyPercent,
        "Total Plays" to gameTotalsPlays,
        "Total Sacks" to passingSacked,
        "Total Punts" to puntingPunts,
        "Total Penalties" to totalPenalties,
        "Penalty Yards" to penaltiesYards,
        "Fumble Lost" to fumbleLost,
        "Interception Attempted" to interceptionReturnAttempts,
        "Possession Time" to "$timeOfPossessionMinutes:$timeOfPossessionSeconds"
    )
}