package tv.accedo.dishonstream2.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tv.accedo.dishonstream2.data.db.dao.ChannelDao
import tv.accedo.dishonstream2.data.db.model.ChannelEntity

@Database(version = 1, entities = [ChannelEntity::class], exportSchema = false)
abstract class DishDatabase : RoomDatabase() {

    abstract fun channelDao(): ChannelDao

    companion object {

        @Volatile
        private var INSTANCE: DishDatabase? = null

        fun getDatabase(context: Context): DishDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    DishDatabase::class.java,
                    "dishdatabase.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }

}