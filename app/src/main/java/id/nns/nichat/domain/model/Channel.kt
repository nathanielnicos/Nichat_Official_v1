package id.nns.nichat.domain.model

data class Channel(
    val partnerId: String = "",
    val partnerUser: User? = null,
    val latestMessage: Message? = null
)