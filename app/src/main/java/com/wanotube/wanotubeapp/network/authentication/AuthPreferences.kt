package com.wanotube.wanotubeapp.network.authentication

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val accountName: String?
        get() = preferences.getString(KEY_ACCOUNT_NAME, null)
    var authToken: String?
        get() = preferences.getString(KEY_AUTH_TOKEN, null)
        set(authToken) {
            val editor = preferences.edit()
            editor.putString(KEY_AUTH_TOKEN, authToken)
            editor.apply()
        }

    fun setUsername(accountName: String?) {
        val editor = preferences.edit()
        editor.putString(KEY_ACCOUNT_NAME, accountName)
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "auth"
        private const val KEY_ACCOUNT_NAME = "account_name"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

}