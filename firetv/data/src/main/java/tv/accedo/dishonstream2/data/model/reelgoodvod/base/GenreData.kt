package tv.accedo.dishonstream2.data.model.reelgoodvod.base

data class GenreResult(
    val result: List<GenreData>
)

data class GenreData(
    val id: Int,
    val name: String
)