package id.nns.nichat.local.channel

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.nns.nichat.data.entity.ChannelEntity

@Database(entities = [ChannelEntity::class], version = 1, exportSchema = false)
abstract class ChannelDatabase : RoomDatabase() {

    abstract fun channelDao() : ChannelDao

    companion object {
        @Volatile
        private var INSTANCE: ChannelDatabase? = null

        fun getInstance(context: Context) : ChannelDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ChannelDatabase::class.java,
                    "channel_database"
                ).build().apply {
                    INSTANCE = this
                }
            }
    }

}