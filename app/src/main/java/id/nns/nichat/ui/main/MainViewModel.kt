package id.nns.nichat.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import id.nns.nichat.data.notification.Token
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.data.response.UserResponse
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import id.nns.nichat.utils.firebase_utils.MessagingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _isFinish = MutableLiveData<Boolean>()
    val isFinish: LiveData<Boolean> get() = _isFinish

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun onResultOk(preference: UserPreference) {
        val newUser = UserResponse(
            uid = FirebaseAuth.getInstance().currentUser?.uid.toString(),
            name = FirebaseAuth.getInstance().currentUser?.displayName.toString(),
            email = FirebaseAuth.getInstance().currentUser?.email.toString()
        )

        FirestoreUtil.checkCurrentUser { currentUserDocRef, isExist, msg ->
            if (msg != null) {
                _message.value = msg
            }

            if (!isExist) {
                currentUserDocRef.set(newUser)
                    .addOnSuccessListener {
                        saveToken()
                        saveToPref(preference, newUser)
                    }
                    .addOnFailureListener {
                        _message.value = it.message
                    }
            } else {
                updateToken()
                saveToPref(preference, newUser)
            }
        }
    }

    private fun saveToken() {
        MessagingUtil.getToken { token ->
            FirestoreUtil.saveToken(
                Token(
                    uid = FirebaseAuth.getInstance().uid,
                    token = token
                )
            )
        }
    }

    private fun updateToken() {
        MessagingUtil.getToken { token ->
            FirestoreUtil.updateToken(token)
        }
    }

    private fun saveToPref(preference: UserPreference, newUser: UserResponse) {
        FirestoreUtil.getCurrentUser { existingUser ->
            viewModelScope.launch(Dispatchers.IO) {
                if (existingUser?.imgUrl != null) {
                    preference.setUser(existingUser)
                } else {
                    preference.setUser(newUser)
                }

                withContext(Dispatchers.Main) {
                    _isFinish.value = true
                }
            }
        }
    }

}
