package com.wanotube.wanotubeapp.ui.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.WanotubeApp
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.databinding.ActivityEditVideoInfoBinding
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.NetworkVideo
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.repository.VideosRepository
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
    
    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityEditVideoInfoBinding.inflate(layoutInflater)
    }
    
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        
        titleText = viewBinding.videoTitle
        descriptionText = viewBinding.videoDescription

        videosRepository = VideosRepository(getDatabase(application))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        videoId = intent.getStringExtra("VIDEO_ID")
        
        getVideo()
        initComponents()
    }

    public override fun onStart() {
        super.onStart()
        //Support multiple windows
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        //Android API level 24 and lower requires you to wait as long as possible until you grab resources, so you wait until onResume before initializing the player.
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        
        // SDK < 24 No guarantee of onStop being called so we call releasePlayer whenever onPause
        // SDK >= 24 Guarantee being called, in paused state the activity is still visible
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }


    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    // Support full-screen
    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        viewBinding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun initializePlayer() {
        if (!::video.isInitialized)
            return 
        player = SimpleExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(video.url)
                exoPlayer.setMediaItem(mediaItem)

                //playWhenReady tells the player whether to start playing as soon as all resources for playback have been acquired. Because playWhenReady is initially true, playback starts automatically the first time the app is run.
                exoPlayer.playWhenReady = playWhenReady
                //seekTo tells the player to seek to a certain position within a specific window.
                //Both currentWindow and playbackPosition are initialized to zero so that playback starts from the very start the first time the app is run.
                exoPlayer.seekTo(currentWindow, playbackPosition)
                //prepare tells the player to acquire all the resources required for playback.
                exoPlayer.prepare()
            }
    }
    
    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
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
                                if (Util.SDK_INT >= 24) {
                                    initializePlayer()
                                }
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