package tv.accedo.dishonstream2.data.model.game

import tv.accedo.dishonstream2.domain.model.home.game.Team

data class TeamData(
    val alias: String,
    val city: String,
    val echoTeamId: String,
    val id: Int,
    val img: String?,
    val name: String,
    val state: String,
) {
    fun toTeam(): Team = Team(
        alias,
        city,
        img,
        name
    )
}