package com.wanotube.wanotubeapp

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.network.objects.NetworkVideo
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.ui.edit.EditInfoActivity
import com.wanotube.wanotubeapp.ui.edit.UploadActivity
import com.wanotube.wanotubeapp.ui.login.LoginActivity
import com.wanotube.wanotubeapp.util.Constant.URL
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.Transport
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.io.File

abstract class WanoTubeActivity : AppCompatActivity(){

    protected var mAccountManager: AccountManager? = null
    protected var mAuthPreferences: AuthPreferences? = null
    protected var authToken: String? = null
    private lateinit var mSocket: Socket
    lateinit var progressBar: ProgressBar
    lateinit var dialog: AlertDialog
    var isUploading = false
    
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
        val result = account != null && token != null
        if (!result) {
            openLoginActivity()
        }
        return result
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

    
    fun uploadVideo(filePath: String, isUploadNormalVideo: Boolean) {
        val videosRepository = VideosRepository(getDatabase(application))

        val file = File(filePath)
        
        val mAuthPreferences = AuthPreferences(this)
        val context = this
        mAuthPreferences.authToken?.let {
            val responseBodyCall = videosRepository.uploadVideo(
                file,
                it,
                isUploadNormalVideo
            )
            responseBodyCall?.enqueue(object : Callback<NetworkVideo> {
                override fun onResponse(
                    call: Call<NetworkVideo>,
                    response: Response<NetworkVideo>,
                ) {
                    if (response.code() == 200) {
                        val body = response.body()?.asDatabaseModel()
                        if (body != null) {
                            val intent = Intent(context, EditInfoActivity::class.java)
                            intent.putExtra("VIDEO_ID", body.id)
                            context.startActivity(intent)
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<NetworkVideo>?, t: Throwable?) {
                    Timber.e("Failed: %s", t.toString())
                }
            })
            startServerSocket()
        }
    }

    fun startServerSocket() {
        try {
            mSocket = IO.socket(URL)
            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e("fail %s", "Failed to connect")
        }
    }

    private var onConnect = Emitter.Listener {
        Timber.e("Video's upload state: %s", "On connect")
        mSocket.on(UploadActivity.UPLOAD_TO_S3, onUpload)
    }

    private var onUpload = Emitter.Listener { args ->
        runOnUiThread {
            setProgressDialog()
        }
        Timber.e("Video's upload progress %s", "Progress: " + args[0])
        if (args != null) {
            if (args[0] != null) {
                if (args[0] is Int) {
                    val progress = args[0] as Int
                    runOnUiThread {
                        progressBar.progress = progress
                    }
                    if (progress >= 100)
                        finishUploading()
                } else if (args[0] is Float) {
                    val progress = args[0] as Float
                    runOnUiThread {
                        progressBar.progress = progress.toInt()
                    }
                    if (progress >= 100f)
                        finishUploading()
                }
            }
        }
    }
    
    private fun finishUploading() {
        mSocket.off()
        runOnUiThread {
            progressBar.visibility = View.GONE
            dialog.hide()
        }
    }

    // Function to display ProgressBar
    // inside AlertDialog
    private fun setProgressDialog() {
        if (!isUploading) {
            isUploading = true
            // Creating a Linear Layout
            val llPadding = 30
            val ll = LinearLayout(this)
            ll.orientation = LinearLayout.VERTICAL
            ll.setPadding(llPadding, llPadding, llPadding, llPadding)
            ll.gravity = Gravity.CENTER
            var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER
            ll.layoutParams = llParam

            // Creating a ProgressBar inside the layout
            progressBar = ProgressBar(this)
            progressBar.isIndeterminate = true
            progressBar.setPadding(0, 0, llPadding, 0)
            progressBar.layoutParams = llParam
            llParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER

            // Creating a TextView inside the layout
            val tvText = TextView(this)
            tvText.text = "Loading ..."
            tvText.setTextColor(Color.parseColor("#000000"))
            tvText.textSize = 20f
            tvText.layoutParams = llParam
            ll.addView(progressBar)
            ll.addView(tvText)

            // Setting the AlertDialog Builder view 
            // as the Linear layout created above
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setView(ll)

            // Displaying the dialog
            dialog = builder.create()
            dialog.show()

            val window: Window? = dialog.window
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog.window?.attributes)
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                dialog.window?.attributes = layoutParams

                // Disabling screen touch to avoid exiting the Dialog
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::mSocket.isInitialized) {
            mSocket.off()
            mSocket.disconnect()
        }
    }
}
