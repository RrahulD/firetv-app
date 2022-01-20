package tv.accedo.dishonstream2.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tv.accedo.dishonstream2.data.db.model.ChannelEntity

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channel")
    suspend fun getAllChannels(): List<ChannelEntity>

    @Query("SELECT * FROM channel WHERE id = :id")
    suspend fun getChannelById(id: Int): ChannelEntity?

    @Query("SELECT * FROM channel WHERE serviceName = :serviceName ")
    suspend fun getChannelByServiceName(serviceName: String): ChannelEntity?

    @Query("SELECT * FROM channel WHERE id = :channelId ")
    suspend fun getChannelByChannelId(channelId: Int): ChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channelEntity: ChannelEntity)

    @Query("DELETE FROM channel")
    suspend fun deleteAllChannels()

}