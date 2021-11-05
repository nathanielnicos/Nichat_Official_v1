package id.nns.nichat.domain.model

data class Message(
    val fromId: String? = null,
    val messageId: String? = null,
    val text: String? = null,
    val timeStamp: Long? = null,
    val toId: String? = null
)
