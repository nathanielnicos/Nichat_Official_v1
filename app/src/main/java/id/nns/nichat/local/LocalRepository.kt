package id.nns.nichat.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.utils.DataMapper
import id.nns.nichat.utils.DataMapper.toDomain
import id.nns.nichat.utils.DataMapper.toEntity

class LocalRepository(private val channelDao: ChannelDao) : ILocalRepository {

    companion object {
        @Volatile
        private var INSTANCE: LocalRepository? = null

        fun getInstance(dao: ChannelDao) : LocalRepository =
            INSTANCE ?: LocalRepository(dao)
    }

    override fun getAllChannels() : LiveData<List<Channel>> =
        Transformations.map(channelDao.getAllChannels()) {
            DataMapper.mapChannelEntityToDomain(it)
        }

    override suspend fun getChannelById(partnerId: String) : Channel? =
        channelDao.getChannelById(partnerId)?.toDomain()

    override suspend fun getLatestTextById(partnerId: String) : String =
        channelDao.getLatestTextById(partnerId)

    override suspend fun insertChannel(channel: Channel) =
        channelDao.insertChannel(channel.toEntity())

    override suspend fun deleteChannel(partnerId: String) =
        channelDao.deleteChannel(partnerId)

    override fun getChannelCount() : LiveData<Int> =
        channelDao.channelCount()

}