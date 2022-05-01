package com.wanotube.wanotubeapp.network.authentication

import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.IBinder


class AuthenticatorService : Service() {
    override fun onBind(intent: Intent): IBinder {
        var binder: IBinder? = null
        if (intent.action == AccountManager.ACTION_AUTHENTICATOR_INTENT) {
            binder = authenticator?.iBinder
        }
        return binder!!
    }

    private val authenticator: AccountAuthenticator?
        get() {
            if (null == sAccountAuthenticator) {
                sAccountAuthenticator = AccountAuthenticator(this)
            }
            return sAccountAuthenticator
        }

    companion object {
        private var sAccountAuthenticator: AccountAuthenticator? = null
    }
}
