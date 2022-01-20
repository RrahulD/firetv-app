package tv.accedo.dishonstream2.domain.model.supair

import java.io.Serializable

data class ChannelInfo(
    val id: Int,
    val callSign: String,
    val isHD: Int,
    val serviceType: String,
    val serviceId: Int,
    val logoURL: String
) : Serializable