package id.nns.nichat.ui.chat

import androidx.lifecycle.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import id.nns.nichat.data.notification.NotificationData
import id.nns.nichat.data.notification.PushNotification
import id.nns.nichat.domain.model.Chat
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.remote.RetrofitInstance
import id.nns.nichat.utils.converters.ChatConverters
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import id.nns.nichat.utils.firebase_utils.RealtimeUtil
import id.nns.nichat.utils.firebase_utils.StorageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(private val localRepository: ILocalRepository) : ViewModel() {

    private val partnerId = MutableLiveData<String>()

    fun setPartnerId(partnerId: String) {
        this.partnerId.value = partnerId
    }

    val messages: LiveData<String?> = Transformations.switchMap(partnerId) {
        saveMessagesToLocalDatabase(it)
        localRepository.getMessagesById(it)
    }

    private val _isSend = MutableLiveData<Boolean>()
    val isSend: LiveData<Boolean> get() = _isSend

    private val _imgUrl = MutableLiveData<String?>()
    val imgUrl: LiveData<String?> get() = _imgUrl

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private fun saveMessagesToLocalDatabase(toId: String) {
        val data = ArrayList<Message>()

        RealtimeUtil.getMessageReference(toId).addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value = snapshot.getValue(Message::class.java)

                if (value != null) {
                    data.add(value)
                    val json = ChatConverters.fromListToJson(data)

                    viewModelScope.launch(Dispatchers.IO) {
                        localRepository.insertChat(
                            Chat(
                                partnerId = toId,
                                messages = json
                            )
                        )
                    }
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onChildRemoved(snapshot: DataSnapshot) = Unit
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    fun sendMessage(toId: String, message: String) {
        RealtimeUtil.pushMessageToReference(toId, message) {
            _isSend.value = true
        }
    }

    fun getImageUrl(selectedImageBytes: ByteArray?) {
        if (selectedImageBytes != null) {
            StorageUtil.uploadMessageImage(selectedImageBytes) { url ->
                _imgUrl.value = url
            }
        }
    }

    fun clearChat(toId: String) {
        RealtimeUtil.clearChat(toId) {
            _error.value = it

            // Delete Channel from Database
            deleteChannelFromDatabase(toId)
        }
    }

    private fun deleteChannelFromDatabase(toId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteChannel(toId)
            localRepository.deleteChat(toId)
        }
    }

    fun sendNotification(toId: String, sender: String, text: String) {
        FirestoreUtil.getUserToken(toId) { token ->
            viewModelScope.launch(Dispatchers.IO) {
                val notification = PushNotification(
                    data = NotificationData(
                        sender = sender,
                        text = text
                    ),
                    to = token?.token
                )
                RetrofitInstance.api.postNotification(notification)
            }
        }
    }

}