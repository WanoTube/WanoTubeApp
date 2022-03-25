package com.wanotube.wanotubeapp.ui.watch

import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.TypedValue
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.VideoView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wanotube.wanotubeapp.IOnBackPressed
import com.wanotube.wanotubeapp.IOnFocusListenable
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.databinding.FragmentWatchBinding


class WatchFragment: Fragment(), IOnBackPressed, IOnFocusListenable {
    private lateinit var videoLayout: RelativeLayout
    private lateinit var videoView: VideoView
    private lateinit var forwardImg: ImageView
    private  lateinit var backwardImg:ImageView
    private  lateinit var playBtn:ImageView
    private  lateinit var pauseBtn:ImageView
    private  lateinit var fullscreen:ImageView
    private  lateinit var fullscreenExit:ImageView
    private  lateinit var showImgUp:ImageView
    private  lateinit var showImgDown:ImageView
    private lateinit var mediaControls: RelativeLayout
    private var check = 0
    private lateinit var startTime: TextView
    private  lateinit var endTime:TextView
    private lateinit var rewindTxt: TextView
    private  lateinit var forwardTxt:TextView
    private lateinit var seekBar: SeekBar
    private lateinit var videoHandler: Handler
    private lateinit var videoRunnable: Runnable
    private var countdownTimer: CountDownTimer? = null
    private lateinit var progressBar: ProgressBar
    private  lateinit var bufferbar:ProgressBar
    private lateinit var backFrame:FrameLayout
    private lateinit var forwardFrame:FrameLayout
    private lateinit var dismissControlFrame:FrameLayout

    //    private val youtubeLayout: YoutubeLayout
    private val isMaximise = true
    private var url =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentWatchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_watch, container, false
        )

        initLayouts(binding)
        initialiseSeekBar()
        setHandler()
        initiateVideo()

        return inflater.inflate(R.layout.fragment_watch, container, false)
    }

    private fun initLayouts(binding: FragmentWatchBinding) {
        videoLayout = binding.videoLayout
        videoView = binding.videoPlayer
        forwardImg = binding.frwardImg
        backwardImg = binding.backwardImg
        playBtn = binding.playbtn
        pauseBtn = binding.pausebtn
        fullscreen = binding.fullscreen
        fullscreenExit = binding.fullscreenexit
        showImgUp = binding.showup
        showImgDown = binding.showdown
        mediaControls = binding.mediacontrols
        startTime = binding.starttime
        endTime = binding.endtime
        rewindTxt = binding.rewindtxt
        forwardTxt = binding.frwrdtxt
        seekBar = binding.seekbar
        progressBar = binding.progress
        bufferbar = binding.bufferbar
        backFrame = binding.bbkframe
        forwardFrame = binding.ffrdframe
        dismissControlFrame = binding.dismissControlFrame

        dismissControlFrame.setOnClickListener {
            dismissControls()
        }
    }

    private fun initiateVideo() {
        videoView.setVideoURI(Uri.parse(url))
        videoView.start()

        if (videoView.isPlaying)
            progressBar.visibility = View.VISIBLE;

        videoView.setOnPreparedListener { mp ->
            videoView.start()
            seekBar.max = videoView.duration
            progressBar.visibility = View.GONE
            mp.setOnInfoListener { _, what, _ ->
                when (what) {
                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> progressBar.visibility =
                        View.VISIBLE
                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> progressBar.visibility =
                        View.GONE
                }
                false
            }
            mp.setOnBufferingUpdateListener { _, percent -> bufferbar.progress = percent }
            mp.setOnCompletionListener { }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230.0f, resources.displayMetrics)
                .toInt()
        val params = videoLayout.layoutParams
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                hideSystemUI()
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                fullscreen.visibility = View.GONE
                fullscreenExit.visibility = View.VISIBLE
                videoLayout.requestLayout()
                showImgDown.visibility = View.GONE
                showImgUp.visibility = View.GONE
                if (!isMaximise) {
                    videoView.pause()
                }
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                val decorView: View? = activity?.window?.decorView
                if (decorView != null) {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = height
                fullscreen.visibility = View.VISIBLE
                fullscreenExit.visibility = View.GONE
                videoLayout.requestLayout()
                showImgDown.visibility = View.VISIBLE
                showImgUp.visibility = View.GONE
            }
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val checkConfig: Configuration = resources.configuration
        if (checkConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (hasFocus) {
                hideSystemUI()
            }
        }
    }

    private fun hideSystemUI() {
        val decorView: View? = activity?.window?.decorView
        if (decorView != null) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    fun animForward(view: ImageView) {
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        prepareAnimationForward(scaleAnimation)
        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        prepareAnimationForward(alphaAnimation)
        val animation = AnimationSet(true)
        animation.addAnimation(alphaAnimation)
        animation.addAnimation(scaleAnimation)
        animation.duration = 500 //u can adjust yourself
        view.startAnimation(animation)
    }

    private fun prepareAnimationForward(animation: Animation): Animation? {
        animation.repeatCount = 1
        animation.repeatMode = Animation.REVERSE
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val fadeOut: Animation = AlphaAnimation(1f, 0f)
                fadeOut.interpolator = AccelerateInterpolator()
                fadeOut.startOffset = 100
                fadeOut.duration = 100
                val animation1 = AnimationSet(false)
                animation1.addAnimation(fadeOut)
                forwardTxt.animation = animation1
                forwardImg.visibility = View.GONE
                forwardTxt.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        return animation
    }

    private fun animBackward(view: ImageView) {
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        prepareAnimationBackward(scaleAnimation)
        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        prepareAnimationBackward(alphaAnimation)
        val animation = AnimationSet(true)
        animation.addAnimation(alphaAnimation)
        animation.addAnimation(scaleAnimation)
        animation.duration = 500 //u can adjust yourself
        view.startAnimation(animation)
    }

    private fun prepareAnimationBackward(animation: Animation): Animation? {
        animation.repeatCount = 1
        animation.repeatMode = Animation.REVERSE
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val fadeOut: Animation = AlphaAnimation(1f, 0f)
                fadeOut.interpolator = AccelerateInterpolator()
                fadeOut.startOffset = 100
                fadeOut.duration = 100
                val animation1 = AnimationSet(false)
                animation1.addAnimation(fadeOut)
                rewindTxt.animation = animation1
                backwardImg.visibility = View.GONE
                rewindTxt.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        return animation
    }

    fun fastForward() {
        videoView.seekTo(videoView.currentPosition + 10000)
        forwardImg.visibility = View.VISIBLE
        forwardTxt.visibility = View.VISIBLE
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 100
        val animation = AnimationSet(false)
        animation.addAnimation(fadeIn)
        forwardTxt.animation = animation
        animForward(forwardImg)
    }

    fun fastRewind() {
        videoView.seekTo(videoView.currentPosition - 10000)
        backwardImg.visibility = View.VISIBLE
        rewindTxt.visibility = View.VISIBLE
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 100
        val animation = AnimationSet(false)
        animation.addAnimation(fadeIn)
        rewindTxt.animation = animation
        animBackward(backwardImg)
    }

    fun startTimer() {
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 500
        val animation = AnimationSet(false)
        animation.addAnimation(fadeIn)
        mediaControls.animation = animation
        countdownTimer = object : CountDownTimer(1500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mediaControls.visibility = View.VISIBLE
                if (videoView.isPlaying) {
                    pauseBtn.visibility = View.VISIBLE
                    playBtn.visibility = View.GONE
                } else {
                    playBtn.visibility = View.VISIBLE
                    pauseBtn.visibility = View.GONE
                }
            }

            override fun onFinish() {
                val fadeOut: Animation = AlphaAnimation(1f, 0f)
                fadeOut.interpolator = AccelerateInterpolator()
                fadeOut.startOffset = 100
                fadeOut.duration = 100
                val animation = AnimationSet(false)
                animation.addAnimation(fadeOut)
                mediaControls.animation = animation
                mediaControls.visibility = View.GONE
                pauseBtn.visibility = View.GONE
                playBtn.visibility = View.GONE
            }
        }
        (countdownTimer as CountDownTimer).start()
    }

    private fun cancelTimer() {
        countdownTimer?.cancel()
    }

//    protected override fun onDestroy() {
//        cancelTimer()
//        super.onDestroy()
//    }

    fun setHandler() {
        videoHandler = Handler()
        videoRunnable = object : Runnable {
            override fun run() {
                if (videoView.duration > 0) {
                    val currentPosition = videoView.currentPosition
                    seekBar.progress = currentPosition
                    startTime.text = "" + convertIntToTime(currentPosition)
                    endTime.text = "" + convertIntToTime(videoView.duration - currentPosition)
                }
                videoHandler.postDelayed(this, 0)
            }
        }
        videoHandler.postDelayed(videoRunnable, 500)
    }

    private fun convertIntToTime(ms: Int): String {
        var time: String? = null
        val seconds: Int
        val minutes: Int
        val hours: Int
        var x: Int = ms / 1000
        seconds = x % 60
        x /= 60
        minutes = x % 60
        x /= 60
        hours = x % 24
        time = if (hours != 0) String.format("%02d", hours) + ":" + String.format(
            "%02d",
            minutes
        ) + ":" + String.format("%02d", seconds) else String.format(
            "%02d",
            minutes
        ) + ":" + String.format("%02d", seconds)
        return time
    }

    private fun initialiseSeekBar() {
        seekBar.progress = 0
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (seekBar.id == R.id.seekbar) {
                    if (fromUser) {
                        videoView.seekTo(progress)
                        val currentPosition = videoView.currentPosition
                        startTime.text = "" + convertIntToTime(currentPosition)
                        endTime.text = "" + convertIntToTime(videoView.duration - currentPosition)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun releaseVideoPlayer() {
        videoHandler.removeCallbacks(videoRunnable)

//        if (videoView != null) {
//            videoHandler.removeCallbacks(videoRunnable)
//            videoView = null
//        }
    }

    override fun onBackPressed(): Boolean {
        releaseVideoPlayer()
        return true
//        return if (myCondition) {
//            //action not popBackStack
//            true
//        } else {
//            false
//        }
    }

    private fun dismissControls() {
        cancelTimer()
        if (mediaControls.visibility == View.VISIBLE) {
            val fadeOut: Animation = AlphaAnimation(1f, 0f)
            fadeOut.interpolator = AccelerateInterpolator()
            fadeOut.startOffset = 100
            fadeOut.duration = 100
            val animation = AnimationSet(false)
            animation.addAnimation(fadeOut)
            mediaControls.animation = animation
            mediaControls.visibility = View.GONE
            pauseBtn.visibility = View.GONE
            playBtn.visibility = View.GONE
        } else {
            val fadeIn: Animation = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator()
            fadeIn.duration = 500
            val animation = AnimationSet(false)
            animation.addAnimation(fadeIn)
            mediaControls.animation = animation
            mediaControls.visibility = View.VISIBLE
            if (videoView.isPlaying) {
                pauseBtn.visibility = View.VISIBLE
                playBtn.visibility = View.GONE
            } else {
                playBtn.visibility = View.VISIBLE
                pauseBtn.visibility = View.GONE
            }
        }
    }
}