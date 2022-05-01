package com.wanotube.wanotubeapp.network.authentication

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context

object AccountUtils {
    const val ACCOUNT_TYPE = "com.wanotube.wanotubeapp"
    const val AUTH_TOKEN_TYPE = "com.wanotube.wanotubeapp.tokentype"
    
    var mServerAuthenticator: IAuthenticatorService = MyAuthenticatorService()
    
    fun getAccount(context: Context?, accountName: String?): Account? {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE)
        for (account in accounts) {
            if (account.name.equals(accountName, ignoreCase = true)) {
                return account
            }
        }
        return null
    }
}
