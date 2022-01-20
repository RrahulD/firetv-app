package tv.accedo.dishonstream2.domain.model.supair

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RichMediaImageInfo(
    val imageId: Int,
    val imagePath: String,
    val imageType: Int,
    val imageURL: String?,
    val cdn16x9ImageUrl: String?
) : Parcelable