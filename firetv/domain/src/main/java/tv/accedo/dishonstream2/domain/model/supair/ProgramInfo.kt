package tv.accedo.dishonstream2.domain.model.supair

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ProgramInfo(
    val title: String,
    val seriesTitle: String,
    val description: String?,
    val programType:String,
    val rating:String?,
    val richMediaImageInfo: RichMediaImageInfo?
) : Parcelable