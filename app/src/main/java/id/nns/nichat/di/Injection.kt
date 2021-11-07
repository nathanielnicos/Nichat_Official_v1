package id.nns.nichat.di

import android.content.Context
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.local.channel.ChannelDatabase
import id.nns.nichat.local.chat.ChatDatabase
import id.nns.nichat.local.LocalRepository

object Injection {

    fun provideILocalRepository(context: Context) : ILocalRepository {
        val chnDatabase = ChannelDatabase.getInstance(context)
        val chtDatabase = ChatDatabase.getInstance(context)
        return LocalRepository.getInstance(
            chnDatabase.channelDao(),
            chtDatabase.chatDao()
        )
    }

}