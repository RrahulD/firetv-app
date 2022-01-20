package tv.accedo.dishonstream2.data.model.base

import tv.accedo.dishonstream2.domain.model.home.template.base.Image

data class ImageData(
    val fileName: String,
    val size: Long,
    val fileUrl: String,
    val fileId: String
) {
    fun toImage() = Image(
        fileUrl = fileUrl
    )
}