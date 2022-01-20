package tv.accedo.dishonstream2.data.model.reelgoodvod.search


import tv.accedo.dishonstream2.data.model.reelgoodvod.base.VODContentData

data class VODSearch(
    val currentChunk: List<VODContentData>,
    val next: Int?
)