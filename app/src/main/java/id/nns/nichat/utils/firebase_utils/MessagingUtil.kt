package id.nns.nichat.utils.firebase_utils

import com.google.firebase.messaging.FirebaseMessaging

object MessagingUtil {

    private val messagingInstance: FirebaseMessaging by lazy {
        FirebaseMessaging.getInstance()
    }

    fun getToken(token: (String) -> Unit) {
        messagingInstance.token.addOnSuccessListener {
            token(it)
        }
    }

}