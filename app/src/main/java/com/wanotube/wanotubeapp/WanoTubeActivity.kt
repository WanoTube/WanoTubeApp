package com.wanotube.wanotubeapp

import android.accounts.AccountManager
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.ui.login.LoginActivity
import timber.log.Timber
import timber.log.Timber.DebugTree

abstract class WanoTubeActivity : AppCompatActivity(){

    protected var mAccountManager: AccountManager? = null
    protected var mAuthPreferences: AuthPreferences? = null
    protected var authToken: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customActionBar()
        Timber.plant(DebugTree())

        authToken = null
        mAuthPreferences = AuthPreferences(this)
        mAccountManager = AccountManager.get(this)
    }

    open fun customActionBar() {
        supportActionBar!!.apply {
            elevation = 0f
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                if (parentActivityIntent == null) {
                    onBackPressed()
                } else {
                    NavUtils.navigateUpFromSameTask(this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    open fun checkTokenAvailable(): Boolean {
        val email =  mAuthPreferences!!.email
        val account = AccountUtils.getAccount(this, email)
        val token = mAuthPreferences!!.authToken
        return account != null && token != null
    }
    
    open fun logOut() {
        // Clear session and ask for new auth token
        mAccountManager!!.invalidateAuthToken(AccountUtils.ACCOUNT_TYPE, authToken)
        mAuthPreferences!!.authToken = null
        mAuthPreferences!!.email = null
        mAuthPreferences!!.username = null
        
        // Open LoginActivity
        openLoginActivity()
    }
    
    open fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    open fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager =
                getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        } catch (e: Exception) {
            false
        }
    }
}
