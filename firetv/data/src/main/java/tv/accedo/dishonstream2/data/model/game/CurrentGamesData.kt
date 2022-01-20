package tv.accedo.dishonstream2.data.model.game


data class CurrentGamesData(
    val content: List<GameData>,
    val totalItems: Int,
    val totalPages: Int
)