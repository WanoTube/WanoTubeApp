package com.wanotube.wanotubeapp

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.ui.edit.UploadActivity
import com.wanotube.wanotubeapp.ui.login.LoginActivity
import com.wanotube.wanotubeapp.util.Constant
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
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


    private fun setDuration(file: File): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, Uri.fromFile(file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillis = time.toFloat()
        retriever.release()
        return (timeInMillis*1000).toInt()
    }

    fun uploadVideo(filePath: String) {
        val videosRepository = VideosRepository(getDatabase(application))

        val file = File(filePath)
        val videoBody: RequestBody = file.asRequestBody("video/*".toMediaTypeOrNull())
        val fileBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "video",
                file.name,
                videoBody
            )

        val titleBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "title",
                file.name
            )
        
        val sizeBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "size",
//                (file.length() / 1024).toString() //KB
                file.length().toString()//Byte
            )
        
        val descriptionBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "description",
                ""
            )

        val durationBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "duration", setDuration(file).toString())

        val mAuthPreferences = AuthPreferences(this)
        mAuthPreferences.authToken?.let {
            Timber.e("Ngan authToken: " + mAuthPreferences.authToken)
            videosRepository.uploadVideo(
                titleBody,
                sizeBody,
                descriptionBody,
                fileBody,
                durationBody,
                it
            )
        }
        startServerSocket()
    }

    fun startServerSocket() {
        try {
            mSocket = IO.socket("${Constant.BASE_URL}:${Constant.PORT}/")
            Timber.e("Ngan socketId: %s", mSocket.id())
            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e("fail %s", "Failed to connect")
        }
    }

    private var onConnect = Emitter.Listener {
        runOnUiThread {
            setProgressDialog()
        }
        Timber.e("Ngan %s", "On connect")
        mSocket.on(UploadActivity.UPLOAD_TO_S3, onUpload)
    }

    private var onUpload = Emitter.Listener { args ->
        Timber.e("Ngan %s", "Progress: " + args[0])
        if (args != null) {
            if (args[0] != null) {
                val progress = args[0] as Int
                runOnUiThread {
                    progressBar.progress = progress
                }
                if (progress >= 100)
                    finishUploading()
            }
        }
    }
    
    private fun finishUploading() {
        mSocket.off()
        runOnUiThread {
            progressBar.visibility = View.GONE
            dialog.hide()
//            finish()
//            super.onBackPressed()
//            openUploadActivity()
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

    fun openUploadActivity(videoId: String) {
        val intent = Intent(baseContext, UploadActivity::class.java)
        intent.putExtra("VIDEO_ID", videoId)
        startActivity(intent)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::mSocket.isInitialized) {
            mSocket.off()
            mSocket.disconnect()
        }
    }
}
