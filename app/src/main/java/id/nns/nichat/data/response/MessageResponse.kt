package id.nns.nichat.data.response

data class MessageResponse(
    val fromId: String? = null,
    val messageId: String? = null,
    val text: String? = null,
    val timeStamp: Long? = null,
    val toId: String? = null
)
