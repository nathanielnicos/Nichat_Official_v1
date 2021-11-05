package id.nns.nichat.ui.home

import androidx.lifecycle.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.data.response.MessageResponse
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.utils.DataMapper.toDomain
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import id.nns.nichat.utils.firebase_utils.RealtimeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val localRepository: ILocalRepository) : ViewModel() {
    private val _channelsMap = MutableLiveData<HashMap<String, Channel>>()
    private val map = HashMap<String, Channel>()

    val channels: LiveData<List<Channel>> = localRepository.getAllChannels()
    val channelsCount: LiveData<Int> = localRepository.getChannelCount()

    fun updateChannels(isOnline: Boolean) {
        if (isOnline) {
            getChannelsFromRealtimeDatabase()
        }
    }

    private fun getChannelsFromRealtimeDatabase() {
        RealtimeUtil.getChannelReference()
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    mapValue(snapshot)
                }
                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    mapValue(snapshot)
                }
                override fun onChildRemoved(snapshot: DataSnapshot) = Unit
                override fun onChildMoved(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) = Unit
                override fun onCancelled(error: DatabaseError) = Unit
            })
    }

    private fun mapValue(snapshot: DataSnapshot) {
        val key = snapshot.key.toString()
        val value = snapshot.getValue(MessageResponse::class.java)

        FirestoreUtil.getOtherUser(key) {
            val channel = Channel(
                it?.uid.toString(),
                it?.toDomain(),
                value?.toDomain()
            )

            map[key] = channel

            // Check Local Database
            checkLocalDatabase(channel)

            _channelsMap.value = map
        }
    }

    private fun checkLocalDatabase(channelFromRealtimeDatabase: Channel) {
        viewModelScope.launch(Dispatchers.IO) {
            val channelFromLocalDatabase = localRepository.getChannelById(
                channelFromRealtimeDatabase.partnerUser?.uid.toString()
            )
            if (channelFromLocalDatabase != null) {
                withContext(Dispatchers.Main) {
                    if (channelFromRealtimeDatabase.partnerUser?.name !=
                        channelFromLocalDatabase.partnerUser?.name ||
                        channelFromRealtimeDatabase.partnerUser?.status !=
                        channelFromLocalDatabase.partnerUser?.status ||
                        channelFromRealtimeDatabase.partnerUser?.imgUrl !=
                        channelFromLocalDatabase.partnerUser?.imgUrl ||
                        channelFromRealtimeDatabase.latestMessage?.text !=
                        channelFromLocalDatabase.latestMessage?.text) {

                        saveToLocalDatabase(channelFromRealtimeDatabase)
                    }
                }
            } else {
                saveToLocalDatabase(channelFromRealtimeDatabase)
            }
        }
    }

    private fun saveToLocalDatabase(channelFromRealtimeDatabase: Channel) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertChannel(
                Channel(
                    partnerId = channelFromRealtimeDatabase.partnerUser?.uid ?: "",
                    partnerUser = channelFromRealtimeDatabase.partnerUser,
                    latestMessage = channelFromRealtimeDatabase.latestMessage
                )
            )
        }
    }

}