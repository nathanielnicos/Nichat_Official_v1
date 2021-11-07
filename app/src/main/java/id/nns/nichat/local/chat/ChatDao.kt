package id.nns.nichat.local.chat

import androidx.lifecycle.LiveData
import androidx.room.*
import id.nns.nichat.data.entity.ChatEntity

@Dao
interface ChatDao {

    @Query("SELECT messages FROM chat_table WHERE partner_id = :partnerId")
    fun getMessagesById(partnerId: String) : LiveData<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("DELETE FROM chat_table WHERE partner_id = :partnerId")
    suspend fun deleteChat(partnerId: String)

}