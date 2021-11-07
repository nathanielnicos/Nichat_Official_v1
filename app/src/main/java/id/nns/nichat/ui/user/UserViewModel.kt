package id.nns.nichat.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import id.nns.nichat.domain.model.User
import id.nns.nichat.utils.converters.DataMapper
import id.nns.nichat.utils.firebase_utils.FirestoreUtil

class UserViewModel : ViewModel() {

    val allUsers: LiveData<ArrayList<User>> =
        Transformations.map(FirestoreUtil.getAllUsers()) {
            DataMapper.mapUserResponseToDomain(it) as ArrayList<User>
        }

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query

    fun showSearchedUsers(name: String) {
        _query.value = name
    }

}