package tv.accedo.dishonstream2.data.model.reelgoodvod.base


import com.google.gson.annotations.SerializedName
import tv.accedo.dishonstream2.data.model.reelgoodvod.base.Links

data class Availability(
    val links: Links,
    @SerializedName("service_group_id")
    val serviceGroupId: Int,
    @SerializedName("service_id")
    val serviceId: Int
)