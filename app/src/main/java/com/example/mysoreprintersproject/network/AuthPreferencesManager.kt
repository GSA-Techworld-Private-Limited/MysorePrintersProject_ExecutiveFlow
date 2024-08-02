package com.example.mysoreprintersproject.network

//class AuthPreferenceManager {
//}

import android.content.Context

object AuthPreferencesManager {

    private const val PREF_NAME = "auth_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    fun saveLoginStatus(context: Context, isLoggedIn: Boolean) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

}
