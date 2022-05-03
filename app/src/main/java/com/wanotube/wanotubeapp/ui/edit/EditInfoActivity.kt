package com.wanotube.wanotubeapp.ui.edit

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.WanotubeApp
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.NetworkVideo
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.util.stringForTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class EditInfoActivity: WanoTubeActivity() {

    private var videoId: String = ""
    private lateinit var video: Video
    private lateinit var videosRepository: VideosRepository
    
    private lateinit var titleText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var durationText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_video_info)
        
        titleText = findViewById(R.id.video_title)
        descriptionText = findViewById(R.id.video_description)
        durationText = findViewById(R.id.duration)

        videosRepository = VideosRepository(getDatabase(application))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        videoId = intent.getStringExtra("VIDEO_ID")
        
        getVideo()
        initComponents()
    }

    private fun getVideo() {
        CoroutineScope(Dispatchers.IO).launch {
            val responseBodyCall = videosRepository.getVideo(videoId)
            responseBodyCall.enqueue(object : Callback<NetworkVideo> {
                override fun onResponse(
                    call: Call<NetworkVideo>?,
                    response: Response<NetworkVideo?>?
                ) {
                    if (response != null) {
                        if (response.code() == 200) {
                            val databaseVideo = response.body()?.asDatabaseModel()
                            Timber.e("Result: %s", databaseVideo)
                            if (databaseVideo != null) {
                                video = databaseVideo.asDomainModel()
                            }
                        } else {
                            Toast.makeText(WanotubeApp.context, "Find video unsuccessfully, please try again :( ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<NetworkVideo>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }
    
    private fun initComponents() {
        if (!::video.isInitialized)
            return
        titleText.setText(video.title)
        descriptionText.setText(video.description)
        durationText.text = stringForTime(video.duration.toFloat())
    }
    
    override fun customActionBar() {
        super.customActionBar()
        supportActionBar!!.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.normal_action_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.next -> {
            updateVideo()
            // Then go to MyVideos
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun updateVideo() {
        
    }
}