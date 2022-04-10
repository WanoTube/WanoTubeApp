package com.wanotube.wanotubeapp.ui.edit

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.wanotube.wanotubeapp.R


class UploadActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);


        filePath = intent.getStringExtra("FILE_PATH")
        val uri: Uri = Uri.parse(filePath)

        videoView = findViewById(R.id.video_player)
        videoView.setVideoURI(uri)

        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)

        playVideo()
    }

    private fun playVideo() {
        videoView.start()
    }
}

