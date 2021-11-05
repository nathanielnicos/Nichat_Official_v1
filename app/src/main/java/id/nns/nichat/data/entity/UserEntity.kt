package id.nns.nichat.data.entity

import androidx.room.ColumnInfo

data class UserEntity(
    @ColumnInfo(name = "dob")
    val dob: String = "--/--/----",

    @ColumnInfo(name = "email")
    val email: String? = null,

    @ColumnInfo(name = "img_url")
    val imgUrl: String? = null,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "",

    @ColumnInfo(name = "uid")
    val uid: String? = null,
)
