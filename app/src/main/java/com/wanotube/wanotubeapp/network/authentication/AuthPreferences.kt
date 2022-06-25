package com.wanotube.wanotubeapp.network.authentication

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = preferences.edit()


    var userId: String?
        get() = preferences.getString(KEY_USER_ID, null)
        set(userId) {
            editor.putString(KEY_USER_ID, userId)
            editor.apply()
        }

    var email: String?
        get() = preferences.getString(KEY_ACCOUNT_NAME, null)
        set(email) {
            editor.putString(KEY_ACCOUNT_NAME, email)
            editor.apply()
        }

    var username: String?
        get() = preferences.getString(KEY_USER_NAME, null)
        set(username) {
            editor.putString(KEY_USER_NAME, username)
            editor.apply()
        }

    var avatar: String?
        get() = preferences.getString(KEY_AVATAR, null)
        set(avatar) {
            editor.putString(KEY_AVATAR, avatar)
            editor.apply()
        }

    var isBlocked: Boolean?
        get() = preferences.getBoolean(KEY_IS_BLOCKED, false)
        set(isBlocked) {
            if (isBlocked != null) {
                editor.putBoolean(KEY_IS_BLOCKED, isBlocked)
                editor.apply()
            }
        }

    var authToken: String?
        get() = preferences.getString(KEY_AUTH_TOKEN, null)
        set(authToken) {
            editor.putString(KEY_AUTH_TOKEN, authToken)
            editor.apply()
        }

    var isAdmin: Boolean?
        get() = preferences.getBoolean(KEY_IS_ADMIN, false)
        set(isAdmin) {
            if (isAdmin != null) {
                editor.putBoolean(KEY_IS_ADMIN, isAdmin)
                editor.apply()
            }
        }

    var accountStatus: String?
        get() = preferences.getString(KEY_ACCOUNT_STATUS, null)
        set(accountStatus) {
            if (accountStatus != null) {
                editor.putString(KEY_ACCOUNT_STATUS, accountStatus)
                editor.apply()
            }
        }


    var strikes: String?
        get() = preferences.getString(KEY_STRIKES, null)
        set(strikes) {
            if (strikes != null) {
                editor.putString(KEY_STRIKES, strikes)
                editor.apply()
            }
        }

    companion object {
        private const val PREFS_NAME = "auth"
        private const val KEY_ACCOUNT_NAME = "account_name" // email
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_AVATAR = "avatar"
        private const val KEY_IS_ADMIN = "is_admin"
        private const val KEY_IS_BLOCKED = "is_blocked"
        private const val KEY_ACCOUNT_STATUS = "account_status"
        private const val KEY_STRIKES = "strikes"

    }
}