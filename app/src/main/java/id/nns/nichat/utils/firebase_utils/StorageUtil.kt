package id.nns.nichat.utils.firebase_utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object StorageUtil {

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val currentUserRef: StorageReference
        get() = storageInstance.reference
            .child(FirebaseAuth.getInstance().uid.toString())

    fun uploadProfileImage(
        imageBytes: ByteArray,
        onSuccess: (imgUrl: String?) -> Unit
    ) {
        val ref = currentUserRef.child("profileImg/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                    .addOnFailureListener {
                        onSuccess(null)
                    }
            }
    }

    fun uploadMessageImage(
        imageBytes: ByteArray,
        onSuccess: (imgUrl: String?) -> Unit
    ) {
        val ref = currentUserRef.child("messageImg/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                    .addOnFailureListener {
                        onSuccess(null)
                    }
            }
    }

}