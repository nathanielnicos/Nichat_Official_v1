package id.nns.nichat.data.entity

import androidx.room.ColumnInfo

data class MessageEntity(
    @ColumnInfo(name = "from_id")
    val fromId: String? = null,

    @ColumnInfo(name = "message_id")
    val messageId: String? = null,

    @ColumnInfo(name = "text")
    val text: String? = null,

    @ColumnInfo(name = "time_stamp")
    val timeStamp: Long? = null,

    @ColumnInfo(name = "to_id")
    val toId: String? = null
)
