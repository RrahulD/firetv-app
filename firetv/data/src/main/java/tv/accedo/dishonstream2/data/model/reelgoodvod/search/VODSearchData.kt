package tv.accedo.dishonstream2.data.model.reelgoodvod.search


data class VODSearchResult(
    val result: VODSearchData
)

data class VODSearchData(
    val vod: List<VODSearch>
)