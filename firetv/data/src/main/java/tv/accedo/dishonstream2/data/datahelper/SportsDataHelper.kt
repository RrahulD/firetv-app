package tv.accedo.dishonstream2.data.datahelper

import com.google.gson.JsonObject
import tv.accedo.dishonstream2.data.model.game.CurrentGamesData
import tv.accedo.dishonstream2.data.model.game.GameData
import tv.accedo.dishonstream2.domain.model.home.game.Game
import tv.accedo.dishonstream2.domain.model.home.game.GameStatus
import tv.accedo.dishonstream2.domain.model.home.game.League
import tv.accedo.dishonstream2.domain.model.home.game.League.*
import tv.accedo.dishonstream2.domain.model.home.game.stat.*
import java.text.SimpleDateFormat
import java.util.*

class SportsDataHelper {
    val scheduleFormat = SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.getDefault())

    fun mapCurrentGamesDataToGameList(currentGamesData: CurrentGamesData): List<Game> {
        return currentGamesData.content.map { mapGameDataToGame(it) }.filterNotNull()
    }

    fun parseGameStatsFromJson(gamesStatsJson: JsonObject, league: League): GameStats {
        val home = gamesStatsJson.getAsJsonObject("homeTeam").getAsJsonObject("stats")
        val away = gamesStatsJson.getAsJsonObject("awayTeam").getAsJsonObject("stats")
        return when (league) {
            NBA -> NBAGameStats(
                home = parseNBAStats(home),
                away = parseNBAStats(away)
            )

            MLB -> MLBGameStats(
                home = parseMLBStats(home),
                away = parseMLBStats(away)
            )

            NFL -> NFLGameStats(
                home = parseNFLStats(home),
                away = parseNFLStats(away)
            )

            NHL -> NHLGameStats(
                home = parseNHLStats(home),
                away = parseNHLStats(away)
            )

            else -> throw IllegalArgumentException("Unknown league passed while parsing game stats")
        }
    }

    private fun parseNBAStats(jsonStats: JsonObject): NBAStats = NBAStats(
        assists = jsonStats.get("assists").asString,
        biggestLead = jsonStats.get("biggestLead").asString,
        blocks = jsonStats.get("blocks").asString,
        fastBreakPts = jsonStats.get("fastBreakPts").asString,
        fieldGoalAttempted = jsonStats.get("fgAttempted").asString,
        fieldGoalMade = jsonStats.get("fgMade").asString,
        fieldGoalPercent = jsonStats.get("fgPct").asString,
        flagrantFouls = jsonStats.get("flagrantFouls").asString,
        fouls = jsonStats.get("fouls").asString,
        freeThrowAttempted = jsonStats.get("ftAttempted").asString,
        freeThrowMade = jsonStats.get("ftMade").asString,
        freeThrowPct = jsonStats.get("ftPct").asString,
        secondChancePoints = jsonStats.get("2ndChancePoints").asString,
        points = jsonStats.get("points").asString,
        pointsInPaint = jsonStats.get("ptsInPaint").asString,
        pointsOffTurnover = jsonStats.get("ptsOffTurnover").asString,
        reboundsDefensive = jsonStats.get("reboundsDefensive").asString,
        reboundsOffensive = jsonStats.get("reboundsOffensive").asString,
        steals = jsonStats.get("steals").asString,
        turnovers = jsonStats.get("turnovers").asString
    )

    private fun parseNFLStats(jsonStats: JsonObject): NFLStats = NFLStats(
        firstDownsNumber = jsonStats.get("firstDowns_number").asString,
        thirdDownEfficiencyPercent = jsonStats.get("thirdDownEfficiency_percent").asString,
        fourthDownEfficiencyPercent = jsonStats.get("fourthDownEfficiency_percent").asString,
        gameTotalsPlays = jsonStats.get("gameTotals_plays").asString,
        gameTotalsNetYards = jsonStats.get("gameTotals_netYards").asString,
        interceptionReturnAttempts = jsonStats.get("interceptionReturn_attempts").asString,
        passingNetYards = jsonStats.get("passing_netYards").asString,
        passingSacked = jsonStats.get("passing_sacked").asString,
        totalPenalties = jsonStats.get("penalties_number").asString,
        penaltiesYards = jsonStats.get("penalties_yards").asString,
        puntingPunts = jsonStats.get("punting_punts").asString,
        rushingYards = jsonStats.get("rushing_yards").asString,
        fumbleLost = jsonStats.get("fumbles_lost").asString,
        timeOfPossessionMinutes = jsonStats.get("timeOfPossession_minutes").asString,
        timeOfPossessionSeconds = jsonStats.get("timeOfPossession_seconds").asString
    )

    private fun parseNHLStats(jsonStats: JsonObject): NHLStats = NHLStats(
        powerplayAttempts = jsonStats.get("powerplay_attempts").asString,
        powerplayGoals = jsonStats.get("powerplay_goals").asString,
        shootoutAttempts = jsonStats.get("shootoutAttempts").asString,
        shootoutScore = jsonStats.get("shootoutScore").asString,
        shots = jsonStats.get("shots").asString
    )

    private fun parseMLBStats(jsonStats: JsonObject): MLBStats = MLBStats(
        bats = jsonStats.get("bats").asString,
        doublePlays = jsonStats.get("doublePlays").asString,
        errors = jsonStats.get("errors").asString,
        hits = jsonStats.get("hits").asString,
        probablePitcher = jsonStats.get("probable_pitcher").asString,
        rbi = jsonStats.get("rbi").asString,
        runnersLeftOnBase = jsonStats.get("runnersLeftOnBase").asString,
        runs = jsonStats.get("runs").asString,
        scoringOpportunities = jsonStats.get("scoringOpportunities").asString,
        scoringSuccesses = jsonStats.get("scoringSuccesses").asString,
        strikeOuts = jsonStats.get("strikeOuts").asString,
        sumBatterLeftOnBase = jsonStats.get("sumBatterLeftOnBase").asString,
        totalBases = jsonStats.get("totalBases").asString,
        triplePlays = jsonStats.get("triplePlays").asString,
        walks = jsonStats.get("walks").asString
    )

    private fun mapGameDataToGame(gameData: GameData): Game? {
        val awayTeam = gameData.awayTeam.toTeam()
        val homeTeam = gameData.homeTeam.toTeam()
        val gameStatus = GameStatus.values().find { it.value == gameData.gameStatus } ?: return null
        val league = values().find { it.value == gameData.league } ?: return null
        val scheduleDate = try {
            val date = scheduleFormat.parse(gameData.scheduledDate)
            date?.time
        } catch (ex: Exception) {
            null
        } ?: return null

        return Game(
            id = gameData.id,
            homeScore = gameData.homeScore,
            awayScore = gameData.awayScore,
            awayTeam = awayTeam,
            displayTime = gameData.displayTime,
            gameStatus = gameStatus,
            homeTeam = homeTeam,
            league = league,
            providerCallsign = gameData.providerCallsign,
            scheduledDate = scheduleDate,
            period = gameData.period,
            venue = gameData.venue
        )
    }
}