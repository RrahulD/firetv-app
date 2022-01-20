package tv.accedo.dishonstream2.data.model.supair

import com.google.gson.annotations.SerializedName
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo

data class ProgramInfoData(
    val title: String,
    @SerializedName("series_title")
    val seriesTitle: String,
    @SerializedName("program_type")
    val programType: String,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("richmedia_image_info") val richMediaImageInfo: RichMediaImageInfoData?,
    val description: String?
) {
    fun toProgramInfoEntity() = ProgramInfo(
        title,
        seriesTitle,
        description,
        programType,
        rating,
        richMediaImageInfo = richMediaImageInfo?.toRichMediaInfoEntity(),
    )
}