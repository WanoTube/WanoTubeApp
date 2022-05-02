package com.wanotube.wanotubeapp

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
        val authToken = mAuthPreferences!!.authToken
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
    
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val ret = super.dispatchTouchEvent(ev)
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_UP) {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        val touchCoordinates = IntArray(2)
                        view.getLocationOnScreen(touchCoordinates)
                        val x: Float = event.rawX + view.getLeft() - touchCoordinates[0]
                        val y: Float = event.rawY + view.getTop() - touchCoordinates[1]
                        //If the touch position is outside the EditText then we hide the keyboard
                        if (x < view.getLeft() || x >= view.getRight() || y < view.getTop() || y > view.getBottom()) {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                            view.clearFocus()
                        }
                    }
                }
            }
        }

        return ret
    }
}
