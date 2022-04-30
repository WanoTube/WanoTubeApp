package com.wanotube.wanotubeapp.ui.edit

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.repository.VideosRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class UploadActivity : WanoTubeActivity() {
    private lateinit var imageView: ImageView
    private lateinit var titleText: EditText
    private lateinit var descriptionText: EditText

    private lateinit var videosRepository: VideosRepository
    private var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        videosRepository = VideosRepository(getDatabase(application))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageView = findViewById(R.id.thumbnail_video_upload)
        titleText = findViewById(R.id.video_title)
        descriptionText = findViewById(R.id.video_description)
        
        filePath = intent.getStringExtra("FILE_PATH")

        Glide.with(this)
            .load(filePath)
            .into(imageView)
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
            uploadVideo()
            finish()
            super.onBackPressed()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    
    private fun uploadVideo() {
        val file = File(filePath)
        val videoBody: RequestBody = file.asRequestBody("video/*".toMediaTypeOrNull())
        val fileBody: MultipartBody.Part = MultipartBody.Part.createFormData("video", file.name, videoBody)
        val titleBody: MultipartBody.Part = MultipartBody.Part.createFormData("title", titleText.text.toString())
        val descriptionBody: MultipartBody.Part = MultipartBody.Part.createFormData("description", descriptionText.text.toString())
        val authorBody: MultipartBody.Part = MultipartBody.Part.createFormData("author_id", "62697a06925d325eb6ef0ab4")
        val durationBody: MultipartBody.Part = MultipartBody.Part.createFormData("duration", "")
        val privacyBody: MultipartBody.Part = MultipartBody.Part.createFormData("privacy", "0")

        videosRepository.uploadVideo(
            titleBody,
            descriptionBody,
            fileBody,
            authorBody,
            durationBody,
            privacyBody
        )
    }
}