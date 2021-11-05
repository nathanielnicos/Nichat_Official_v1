package id.nns.nichat.utils.firebase_utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.nns.nichat.data.notification.Token
import id.nns.nichat.data.response.UserResponse

object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().uid}")

    fun checkCurrentUser(
        isExist: (DocumentReference, Boolean, String?) -> Unit
    ) {
        currentUserDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                isExist(currentUserDocRef, documentSnapshot.exists(), null)
            }
            .addOnFailureListener {
                isExist(currentUserDocRef, false, it.message.toString())
            }
    }

    fun updateCurrentUser(
        name: String = "",
        imgUrl: String? = null,
        status: String = "",
        dob: String = "--/--/----",
        onSuccess: (isSuccess: Boolean) -> Unit
    ) {
        val userFieldMap = mutableMapOf<String, Any>()

        if (name.isNotBlank()) userFieldMap["name"] = name
        if (imgUrl != null) userFieldMap["imgUrl"] = imgUrl
        if (status.isNotBlank()) userFieldMap["status"] = status
        if (dob.isNotBlank()) userFieldMap["dob"] = dob

        currentUserDocRef.update(userFieldMap)
            .addOnSuccessListener {
                onSuccess(true)
            }
            .addOnFailureListener {
                onSuccess(false)
            }
    }

    fun getCurrentUser(onSuccess: (UserResponse?) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onSuccess(it.toObject(UserResponse::class.java))
            }
    }

    fun getOtherUser(toId: String?, onSuccess: (UserResponse?) -> Unit) {
        firestoreInstance.document("users/$toId").get()
            .addOnSuccessListener {
                onSuccess(it.toObject(UserResponse::class.java))
            }
    }

    fun getAllUsers() : LiveData<ArrayList<UserResponse>> {
        val allUsers = MutableLiveData<ArrayList<UserResponse>>()

        firestoreInstance
            .collection("users")
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, _ ->
                val arrayList = ArrayList<UserResponse>()

                querySnapshot?.documents?.forEach { docSnapshot ->
                    if (docSnapshot.id != FirebaseAuth.getInstance().uid) {
                        val user = docSnapshot.toObject(UserResponse::class.java)

                        if (user != null) {
                            arrayList.add(user)
                        }
                    }
                }

                allUsers.value = arrayList
            }

        return allUsers
    }

    private val userTokenDocRef: DocumentReference
        get() = firestoreInstance.document("tokens/${FirebaseAuth.getInstance().uid}")

    fun saveToken(token: Token) {
        userTokenDocRef.set(token)
    }

    fun updateToken(
        newToken: String? = null
    ) {
        val tokenFieldMap = mutableMapOf<String, Any>()

        if (newToken != null) tokenFieldMap["token"] = newToken

        userTokenDocRef.update(tokenFieldMap)
    }

    fun getUserToken(toId: String?, onSuccess: (Token?) -> Unit) {
        firestoreInstance.document("tokens/$toId").get()
            .addOnSuccessListener {
                onSuccess(it.toObject(Token::class.java))
            }
    }

}