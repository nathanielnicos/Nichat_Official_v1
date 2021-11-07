package id.nns.nichat.local.channel

import androidx.lifecycle.LiveData
import androidx.room.*
import id.nns.nichat.data.entity.ChannelEntity

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channel_table")
    fun getAllChannels() : LiveData<List<ChannelEntity>>

    @Query("SELECT * FROM channel_table WHERE partner_id = :partnerId")
    suspend fun getChannelById(partnerId: String) : ChannelEntity?

    @Query("SELECT text FROM channel_table WHERE partner_id = :partnerId")
    suspend fun getLatestTextById(partnerId: String) : String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)

    @Query("DELETE FROM channel_table WHERE partner_id = :partnerId")
    suspend fun deleteChannel(partnerId: String)

    @Query("SELECT COUNT() FROM channel_table")
    fun channelCount() : LiveData<Int>

}