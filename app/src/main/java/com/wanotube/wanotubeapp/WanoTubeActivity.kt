package com.wanotube.wanotubeapp

import android.accounts.AccountManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
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
}
