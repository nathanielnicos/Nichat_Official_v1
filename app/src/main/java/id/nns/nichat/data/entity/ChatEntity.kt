package id.nns.nichat.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_table")
data class ChatEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "partner_id")
    val partnerId: String,

    @ColumnInfo(name = "messages")
    val messages: String? = null
)
