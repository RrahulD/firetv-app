package tv.accedo.dishonstream2.domain.model.supair

data class DRM(
    val contentID:String,
    val drmToken:String,
    val serviceUrlDASH:String,
    val serviceUrlHLS:String
)