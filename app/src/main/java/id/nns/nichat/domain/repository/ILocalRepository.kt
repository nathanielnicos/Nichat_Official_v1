package id.nns.nichat.domain.repository

import androidx.lifecycle.LiveData
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.Chat

interface ILocalRepository {

    fun getAllChannels() : LiveData<List<Channel>>

    suspend fun getChannelById(partnerId: String) : Channel?

    suspend fun getLatestTextById(partnerId: String) : String

    suspend fun insertChannel(channel: Channel)

    suspend fun deleteChannel(partnerId: String)

    fun getChannelCount() : LiveData<Int>

    fun getMessagesById(partnerId: String) : LiveData<String?>

    suspend fun insertChat(chat: Chat)

    suspend fun deleteChat(partnerId: String)

}