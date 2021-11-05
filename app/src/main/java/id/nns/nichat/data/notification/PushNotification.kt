package id.nns.nichat.data.notification

// Don't change val name!
data class PushNotification(
    val data: NotificationData,
    val to: String? = ""
)
