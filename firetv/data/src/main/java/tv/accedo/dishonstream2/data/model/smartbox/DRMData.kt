package tv.accedo.dishonstream2.data.model.smartbox

import tv.accedo.dishonstream2.domain.model.supair.DRM

data class DRMData(
    val contentID: String,
    val drmToken: String,
    val serviceUrlDASH: String,
    val serviceUrlHLS: String
) {
    fun toDRMEntity() = DRM(contentID, drmToken, serviceUrlDASH, serviceUrlHLS)
}