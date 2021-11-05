package id.nns.nichat.utils.firebase_utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import id.nns.nichat.data.response.MessageResponse

object RealtimeUtil {

    private val realtimeInstance: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    fun getMessageReference(toId: String) : DatabaseReference {
        return realtimeInstance.getReference("/chats/${FirebaseAuth.getInstance().uid}/$toId")
    }

    fun pushMessageToReference(
        toId: String,
        text: String,
        onSuccess: () -> Unit
    ) {
        val timeStamp = System.currentTimeMillis() / 1000

        val fromMeReference = realtimeInstance
            .getReference("/chats/${FirebaseAuth.getInstance().uid}/$toId").push()
        val fromYouReference = realtimeInstance
            .getReference("/chats/$toId/${FirebaseAuth.getInstance().uid}").push()
        val latestFromMeReference = realtimeInstance
            .getReference("/channels/${FirebaseAuth.getInstance().uid}/$toId")
        val latestFromYouReference = realtimeInstance
            .getReference("/channels/$toId/${FirebaseAuth.getInstance().uid}")

        val message = MessageResponse(
            fromId = FirebaseAuth.getInstance().uid,
            messageId = fromMeReference.key.toString(),
            text = text,
            timeStamp = timeStamp,
            toId = toId
        )

        fromMeReference.setValue(message)
            .addOnSuccessListener {
                onSuccess()
            }
        fromYouReference.setValue(message)

        latestFromMeReference.setValue(message)
        latestFromYouReference.setValue(message)
    }

    fun getChannelReference() : DatabaseReference {
        return realtimeInstance
            .getReference("/channels/${FirebaseAuth.getInstance().uid}")
    }

    fun clearChat(toId: String, soWhat: (String?) -> Unit) {
        val reference = realtimeInstance
            .getReference("/chats/${FirebaseAuth.getInstance().uid}/$toId")
        val latestReference = realtimeInstance
            .getReference("/channels/${FirebaseAuth.getInstance().uid}/$toId")

        reference.removeValue()
            .addOnSuccessListener {
                latestReference.removeValue()
                    .addOnSuccessListener {
                        soWhat(null)
                    }
                    .addOnFailureListener {
                        soWhat(it.message)
                    }
            }
            .addOnFailureListener {
                soWhat(it.message)
            }
    }

}