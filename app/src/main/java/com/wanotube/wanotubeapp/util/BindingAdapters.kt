package com.wanotube.wanotubeapp.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.components.videoPlayer.PlayerStateCallback

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "playlist")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, playlist: Any?) {
    view.visibility = if (playlist != null) View.GONE else View.VISIBLE

    if(isNetWorkError) {
        view.visibility = View.GONE
    }
}

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String) {
    Glide.with(imageView.context)
        .load(url)
        .placeholder(R.drawable.video_placeholder)
        .into(imageView)
}


private var currentWindow = 0
private var playbackPosition = 0L

@BindingAdapter("video_url", "on_state_change", "is_playing")
fun PlayerView.loadVideo(url: String?, callback: PlayerStateCallback, isPlaying: Boolean) {
    if (url == null) return
    val player = ExoPlayerFactory.newSimpleInstance(
        context, DefaultRenderersFactory(context), DefaultTrackSelector(),
        DefaultLoadControl()
    )

    player.repeatMode = Player.REPEAT_MODE_ALL
    // When changing track, retain the latest frame instead of showing a black screen
    setKeepContentOnPlayerReset(true)
    // We'll show the controller
    this.useController = true
    // Provide url to load the video from here
    val mediaItem = MediaItem.fromUri(url)
    player.setMediaItem(mediaItem)
    //    player.playWhenReady = isPlaying

    player.seekTo(currentWindow, playbackPosition)
    player.prepare()
    
    this.player = player
    
    this.player!!.addListener(object : Player.EventListener {

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
//            this@loadVideo.context.toast("Oops! Error occurred while playing media.")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)

            if (playbackState == Player.STATE_BUFFERING) callback.onVideoBuffering(player) // Buffering.. set progress bar visible here
            if (playbackState == Player.STATE_READY){
                // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
                callback.onVideoDurationRetrieved((this@loadVideo.player as SimpleExoPlayer).duration, player)
            }
            if (playbackState == Player.STATE_READY && player.playWhenReady){
                // [PlayerView] has started playing/resumed the video
                callback.onStartedPlaying(player)
            }
        }
    })
}