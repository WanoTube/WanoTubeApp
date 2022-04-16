package com.wanotube.wanotubeapp.ui.edit

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity

class UploadActivity : WanoTubeActivity() {
    private lateinit var imageView: ImageView
    var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageView = findViewById(R.id.thumbnail_video_upload)
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

}