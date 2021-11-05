package id.nns.nichat.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import id.nns.nichat.data.notification.NotificationData
import id.nns.nichat.data.notification.PushNotification
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.remote.RetrofitInstance
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import id.nns.nichat.utils.firebase_utils.RealtimeUtil
import id.nns.nichat.utils.firebase_utils.StorageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(private val localRepository: ILocalRepository) : ViewModel() {

    private val _isSend = MutableLiveData<Boolean>()
    val isSend: LiveData<Boolean> get() = _isSend

    private val _imgUrl = MutableLiveData<String?>()
    val imgUrl: LiveData<String?> get() = _imgUrl

    private val _messages = MutableLiveData<ArrayList<Message>>()
    val messages: LiveData<ArrayList<Message>> get() = _messages

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getAllMessages(toId: String) {
        val data = ArrayList<Message>()

        RealtimeUtil.getMessageReference(toId).addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value = snapshot.getValue(Message::class.java)

                if (value != null) {
                    data.add(value)
                    _messages.value = data
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
        }
    }

    fun sendNotification(toId: String, sender: String, text: String) {
        FirestoreUtil.getUserToken(toId) { token ->
            val notification = PushNotification(
                data = NotificationData(
                    sender = sender,
                    text = text
                ),
                to = token?.token
            )
            viewModelScope.launch(Dispatchers.IO) {
                RetrofitInstance.api.postNotification(notification)
//                try {
//                    val response = RetrofitInstance.api.postNotification(notification)
//                    if (response.isSuccessful) {
//                        Log.d("Rasengan", "try success")
//                    } else {
//                        Log.d("Rasengan", "try failed: ${response.errorBody()?.string()}")
//                    }
//                } catch (e: Exception) {
//                    Log.d("Rasengan", "catch failed: ${e.message}")
//                }
            }
        }
    }

}