package id.nns.nichat.di

import android.content.Context
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.local.ChannelDatabase
import id.nns.nichat.local.LocalRepository

object Injection {

    fun provideILocalRepository(context: Context) : ILocalRepository {
        val database = ChannelDatabase.getInstance(context)
        return LocalRepository.getInstance(database.channelDao())
    }

}