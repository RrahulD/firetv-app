package tv.accedo.dishonstream2.data.model.supair

import com.google.gson.annotations.SerializedName
import tv.accedo.dishonstream2.domain.model.supair.RichMediaImageInfo

data class RichMediaImageInfoData(
    @SerializedName("image_id") val imageId: Int,
    @SerializedName("image_path") val imagePath: String,
    @SerializedName("image_type") val imageType: Int,
    @SerializedName("image_url") val imageURL: String?,
    @SerializedName("cdn_16x9_url") val _16x9_imageUrl: String?
) {
    fun toRichMediaInfoEntity() = RichMediaImageInfo(
        imageId,
        imagePath,
        imageType,
        imageURL?.replace("http", "https"),
        _16x9_imageUrl?.replace("http", "https")
    )
}