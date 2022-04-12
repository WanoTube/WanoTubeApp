package com.wanotube.wanotubeapp.ui.edit

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R


class UploadActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.thumbnail_video_upload)
        filePath = intent.getStringExtra("FILE_PATH")

        Glide.with(this)
            .load(filePath)
            .into(imageView)
    }

}