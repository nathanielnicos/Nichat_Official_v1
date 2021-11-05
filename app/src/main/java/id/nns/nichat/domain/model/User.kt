package id.nns.nichat.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val dob: String = "--/--/----",
    val email: String? = null,
    val imgUrl: String? = null,
    val name: String? = null,
    val status: String = "",
    val uid: String? = null,
) : Parcelable
