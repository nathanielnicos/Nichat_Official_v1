package id.nns.nichat.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.Chat
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.local.channel.ChannelDao
import id.nns.nichat.local.chat.ChatDao
import id.nns.nichat.utils.converters.DataMapper
import id.nns.nichat.utils.converters.DataMapper.toDomain
import id.nns.nichat.utils.converters.DataMapper.toEntity

class LocalRepository(
    private val channelDao: ChannelDao,
    private val chatDao: ChatDao
) : ILocalRepository {

    companion object {
        @Volatile
        private var INSTANCE: LocalRepository? = null

        fun getInstance(chnDao: ChannelDao, chtDao: ChatDao): LocalRepository =
            INSTANCE ?: LocalRepository(chnDao, chtDao)
    }

    override fun getAllChannels(): LiveData<List<Channel>> =
        Transformations.map(channelDao.getAllChannels()) {
            DataMapper.mapChannelEntityToDomain(it)
        }

    override suspend fun getChannelById(partnerId: String): Channel? =
        channelDao.getChannelById(partnerId)?.toDomain()

    override suspend fun getLatestTextById(partnerId: String): String =
        channelDao.getLatestTextById(partnerId)

    override suspend fun insertChannel(channel: Channel) =
        channelDao.insertChannel(channel.toEntity())

    override suspend fun deleteChannel(partnerId: String) =
        channelDao.deleteChannel(partnerId)

    override fun getChannelCount(): LiveData<Int> =
        channelDao.channelCount()

    override fun getMessagesById(partnerId: String): LiveData<String?> =
        chatDao.getMessagesById(partnerId)

    override suspend fun insertChat(chat: Chat) =
        chatDao.insertChat(chat.toEntity())

    override suspend fun deleteChat(partnerId: String) =
        chatDao.deleteChat(partnerId)

}