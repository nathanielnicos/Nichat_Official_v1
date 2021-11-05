package id.nns.nichat.ui.my_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import id.nns.nichat.utils.firebase_utils.StorageUtil

class ProfileViewModel : ViewModel() {

    private val _isSuccessUpdate = MutableLiveData<Boolean>()
    val isSuccessUpdate: LiveData<Boolean> get() = _isSuccessUpdate

    private val _imgUrl = MutableLiveData<String?>()
    val imgUrl: LiveData<String?> get() = _imgUrl

    fun saveChanges(selectedImageBytes: ByteArray?, name: String, status: String, dob: String) {
        if (selectedImageBytes != null) {
            StorageUtil.uploadProfileImage(selectedImageBytes) { url ->
                if (url != null) {
                    _imgUrl.value = url
                    FirestoreUtil.updateCurrentUser(
                        name = name,
                        imgUrl = url,
                        status = status,
                        dob = dob
                    ) { successUpdate ->
                        _isSuccessUpdate.value = successUpdate
                    }
                } else {
                    _imgUrl.value = null
                }
            }
        } else {
            _imgUrl.value = null
            FirestoreUtil.updateCurrentUser(
                name = name,
                imgUrl = null,
                status = status,
                dob = dob
            ) { successUpdate ->
                _isSuccessUpdate.value = successUpdate
            }
        }
    }

}