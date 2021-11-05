package id.nns.nichat.data.response

data class UserResponse(
    var dob: String = "--/--/----",
    var email: String? = null,
    var imgUrl: String? = null,
    var name: String? = null,
    var status: String = "",
    var uid: String? = null,
)
