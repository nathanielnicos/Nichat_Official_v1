package id.nns.nichat.preference

import android.content.Context
import android.content.SharedPreferences

class OnBoardingPreference(context: Context) {

    companion object {
        private const val PREFS_ON_BOARDING = "on_boarding_pref"
        private const val IS_FIRST_INSTALL = "is_first_install"
    }

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_ON_BOARDING, Context.MODE_PRIVATE)
    }

    var isFirstInstall = preferences.getBoolean(IS_FIRST_INSTALL, true)
        set(value) = preferences.edit().putBoolean(IS_FIRST_INSTALL, value).apply()

}