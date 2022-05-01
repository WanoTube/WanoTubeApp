package com.wanotube.wanotubeapp.network.authentication

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.os.Bundle

class AccountAuthenticator(private val mContext: Context) :
    AbstractAccountAuthenticator(mContext) {
    
    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<String>,
        options: Bundle,
    ): Bundle {
        val reply = Bundle()
//        val intent = Intent(mContext, LoginActivity::class.java)
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
//        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType)
//        intent.putExtra(LoginActivity.ARG_AUTH_TOKEN_TYPE, authTokenType)
//        intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true)

        // return our AccountAuthenticatorActivity
//        reply.putParcelable(AccountManager.KEY_INTENT, intent)
        return reply
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        arg0: AccountAuthenticatorResponse,
        arg1: Account, arg2: Bundle,
    ): Bundle? {
        return null
    }

    override fun editProperties(arg0: AccountAuthenticatorResponse, arg1: String): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle,
    ): Bundle? {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        val am = AccountManager.get(mContext)
        var authToken = am.peekAuthToken(account, authTokenType)

        // TODO: Lets give another try to authenticate the user
//        if (authToken != null) {
//            if (authToken.isEmpty()) {
//                val password = am.getPassword(account)
//                if (password != null) {
//                    authToken = AccountUtils.mServerAuthenticator.login(account.name, password)
//                }
//            }
//        }

        // If we get an authToken - we return it
        if (authToken != null){
            if (authToken.isNotEmpty()) {
                val result = Bundle()
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
                return result
            }
        }
        return null
    }

    override fun getAuthTokenLabel(arg0: String): String? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        arg0: AccountAuthenticatorResponse, arg1: Account,
        arg2: Array<String>,
    ): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        arg0: AccountAuthenticatorResponse,
        arg1: Account, arg2: String, arg3: Bundle,
    ): Bundle? {
        return null
    }
}
