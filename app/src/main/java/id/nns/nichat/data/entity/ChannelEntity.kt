package id.nns.nichat.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel_table")
data class ChannelEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "partner_id")
    val partnerId: String = "",

    @Embedded
    val partnerUser: UserEntity? = null,

    @Embedded
    val latestMessage: MessageEntity? = null
)
