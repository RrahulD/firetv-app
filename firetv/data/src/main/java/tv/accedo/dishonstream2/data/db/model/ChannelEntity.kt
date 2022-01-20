package tv.accedo.dishonstream2.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import tv.accedo.dishonstream2.domain.model.supair.ChannelInfo

@Entity(tableName = "channel")
data class ChannelEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "call_sign") val callSign: String,
    @ColumnInfo(name = "serviceName") val serviceName: String,
    @ColumnInfo(name = "contentID") val contentId: String,
    @ColumnInfo(name = "is_hd") val isHD: Int,
    @ColumnInfo(name = "service_type") val serviceType: String,
    @ColumnInfo(name = "service_id") val serviceId: Int,
    @ColumnInfo(name = "logo_url") val logoURL: String
) {
    fun toChannelInfoEntity() = ChannelInfo(
        id,
        callSign,
        isHD,
        serviceType,
        serviceId,
        logoURL
    )
}