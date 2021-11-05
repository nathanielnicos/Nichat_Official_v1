package id.nns.nichat.preference

import android.content.Context
import android.content.SharedPreferences
import id.nns.nichat.data.response.UserResponse

class UserPreference(context: Context) {

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val UID = "uid"
        private const val NAME = "username"
        private const val IMG_URL = "img_url"
        private const val EMAIL = "email"
        private const val STATUS = "status"
        private const val DOB = "dob"
    }

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setUser(value: UserResponse?) {
        preferences.edit().apply {
            putString(UID, value?.uid)
            putString(NAME, value?.name)
            putString(IMG_URL, value?.imgUrl)
            putString(EMAIL, value?.email)
            putString(STATUS, value?.status)
            putString(DOB, value?.dob)

            apply()
        }
    }

    fun updateUserWithImgUrl(name: String?, imgUrl: String?, status: String?, dob: String) {
        preferences.edit().apply {
            putString(NAME, name)
            putString(IMG_URL, imgUrl)
            putString(STATUS, status)
            putString(DOB, dob)

            apply()
        }
    }

    fun updateUserWithoutImgUrl(name: String, status: String, dob: String) {
        preferences.edit().apply {
            putString(NAME, name)
            putString(STATUS, status)
            putString(DOB, dob)

            apply()
        }
    }

    fun getUser() : UserResponse {
        return UserResponse().apply {
            uid = preferences.getString(UID, "").toString()
            name = preferences.getString(NAME, "").toString()
            imgUrl = preferences.getString(IMG_URL, null)
            email = preferences.getString(EMAIL, "").toString()
            status = preferences.getString(STATUS, "").toString()
            dob = preferences.getString(DOB, "").toString()
        }
    }

    fun clearPrefs() {
        preferences.edit().clear().apply()
    }

}