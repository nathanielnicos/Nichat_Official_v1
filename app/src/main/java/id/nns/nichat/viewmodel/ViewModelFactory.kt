package id.nns.nichat.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.nns.nichat.di.Injection
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.ui.chat.ChatViewModel
import id.nns.nichat.ui.home.HomeViewModel

class ViewModelFactory(private val localRepository: ILocalRepository) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context) : ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                ViewModelFactory(Injection.provideILocalRepository(context))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(localRepository) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(localRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel Class: ${modelClass.name}")
        }
    }

}