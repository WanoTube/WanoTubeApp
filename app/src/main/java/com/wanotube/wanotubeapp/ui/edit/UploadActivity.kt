package com.wanotube.wanotubeapp.ui.edit

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.WanotubeApp.Companion.context
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.util.Constant.BASE_URL
import com.wanotube.wanotubeapp.util.Constant.PORT
import com.wanotube.wanotubeapp.util.stringForTime
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

class UploadActivity : WanoTubeActivity(), AdapterView.OnItemSelectedListener  {
    private lateinit var imageView: ImageView
    private lateinit var titleText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var durationText: TextView

    private lateinit var videosRepository: VideosRepository
    private var filePath: String = ""
    private lateinit var file: File
    
    private var visibility = 0
    private var duration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        videosRepository = VideosRepository(getDatabase(application))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageView = findViewById(R.id.thumbnail_video_upload)
        titleText = findViewById(R.id.video_title)
        descriptionText = findViewById(R.id.video_description)
        progressBar = findViewById(R.id.spinner_loader)
        durationText = findViewById(R.id.duration)

        addResourceForSpinner()
        
        filePath = intent.getStringExtra("FILE_PATH")
        file = File(filePath)
        setDuration()
        Glide.with(this)
            .load(filePath)
            .into(imageView)
    }

    private fun addResourceForSpinner() {
        val privacySpinner = findViewById<Spinner>(R.id.privacy_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.privacy_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            privacySpinner.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        visibility = pos
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
        parent.setSelection(0)
    }
    
    private fun setDuration() {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, Uri.fromFile(file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillis = time.toFloat()
        retriever.release()
        duration = (timeInMillis*1000).toInt()
        durationText.text = stringForTime(timeInMillis)
    }
    
    override fun customActionBar() {
        super.customActionBar()
        supportActionBar!!.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.normal_action_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.next -> {
            // Update DB
            // Then go to MyVideos
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    
    companion object {
        const val UPLOAD_TO_S3 = "Upload to S3"
    }
}