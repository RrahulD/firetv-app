package tv.accedo.dishonstream2.data.model.smartbox

import tv.accedo.dishonstream2.data.model.smartbox.ServiceData
import java.io.Serializable

data class EPGData(
    val numberOfServices: Int,
    val services: List<ServiceData>
):Serializable