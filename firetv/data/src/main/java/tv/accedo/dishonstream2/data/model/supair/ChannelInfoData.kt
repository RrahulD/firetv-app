package tv.accedo.dishonstream2.data.model.supair

import com.google.gson.annotations.SerializedName

data class ChannelInfoData(
    @SerializedName("suid") val suId:Int,
    @SerializedName("callsign") val callSign:String,
    @SerializedName("is_hd") val isHD:Int,
    @SerializedName("service_type") val serviceType:String,
    @SerializedName("service_id") val serviceId:Int,
    @SerializedName("logo_url") val logoURL:String
)