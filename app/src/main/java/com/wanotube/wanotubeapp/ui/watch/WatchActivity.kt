package com.wanotube.wanotubeapp.ui.watch

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.GestureDetector
import android.view.Gravity
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.databinding.ActivityWatchBinding
import com.wanotube.wanotubeapp.domain.WanoTubeVideo
import com.wanotube.wanotubeapp.util.Constant
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel
import timber.log.Timber

class WatchActivity : WanoTubeActivity() {
    private lateinit var binding: ActivityWatchBinding

    private lateinit var videoLayout: RelativeLayout
    private lateinit var videoView: VideoView
    private lateinit var forwardImg: ImageView
    private lateinit var backwardImg: ImageView
    private lateinit var playBtn: ImageView
    private lateinit var pauseBtn: ImageView
    private lateinit var fullscreen: ImageView
    private lateinit var fullscreenExit: ImageView
    private lateinit var showImgUp: ImageView
    private lateinit var showImgDown: ImageView
    private lateinit var mediaControls: RelativeLayout
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var rewindTxt: TextView
    private lateinit var forwardTxt: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var videoHandler: Handler
    private lateinit var videoRunnable: Runnable
    private lateinit var progressBar: ProgressBar
    private lateinit var bufferBar: ProgressBar
    private lateinit var backFrame: FrameLayout
    private lateinit var forwardFrame: FrameLayout
    private lateinit var dismissControlFrame: FrameLayout
    private lateinit var firstCommentSection: LinearLayout
    private lateinit var commentListView: ScrollView
    private lateinit var videoInfoView: ScrollView
    
    private var currentVideo: WanoTubeVideo? = null
    
    private var check = 0
    private val isMaximise = true
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initLayouts(binding)
        setClickListeners()
        initialiseSeekBar()
        setHandler()
        initAdapter()
    }

    override fun customActionBar() {
        super.customActionBar()
        supportActionBar!!.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.normal_action_bar, menu)
//        return true
//    }
    
    private fun initAdapter() {
        val viewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        val videoViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(WanoTubeViewModel::class.java)

        binding.videoViewModel = videoViewModel

        val adapter = WatchAdapter()

        binding.recommendVideoList.adapter = adapter

        binding.lifecycleOwner = this
        
        val videoId = intent.getStringExtra("VIDEO_ID")
        videoViewModel.playlist.observe(this) {
            it?.let {
                adapter.data = it

                currentVideo = it.find { 
                    video -> videoId == video.id
                }
                Timber.e("Ngan %s", "currentVideo: $currentVideo")

                if (currentVideo != null)
                    initVideo()
            }
        } 
    }

    private fun initLayouts(binding: ActivityWatchBinding) {
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
        bufferBar = binding.bufferBar
        backFrame = binding.bbkframe
        forwardFrame = binding.ffrdframe
        dismissControlFrame = binding.dismissControlFrame

        firstCommentSection = binding.firstComment
        commentListView = binding.commentList
        videoInfoView = binding.videoInfoSection
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleVideoPlayer() {

        val gd = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (check == 1) fastRewind() else if (check == 0) fastForward()
                return true
            }

            override fun onLongPress(e: MotionEvent) {}
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                startTimer()
                return super.onSingleTapConfirmed(e)
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float,
            ): Boolean {
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onFling(
                event1: MotionEvent,
                event2: MotionEvent,
                velocityX: Float,
                velocityY: Float,
            ): Boolean {
                //TODO: Minimise and maximise
//                if (isMaximise) {
//                   minimiseView()
//                } else {
//                    maximiseView()
//                }
                return true
            }
        })

        forwardFrame.setOnTouchListener { _, event ->
            check = 1
            gd.onTouchEvent(event)
        }

        backFrame.setOnTouchListener { _, event ->
            check = 0
            gd.onTouchEvent(event)
        }
        
        playBtn.setOnClickListener {
            val fadeIn: Animation = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator()
            fadeIn.duration = 500
            val animation = AnimationSet(false)
            animation.addAnimation(fadeIn)
            pauseBtn.animation = animation
            videoView.start()
            pauseBtn.visibility = View.VISIBLE
            playBtn.visibility = View.GONE
        }

        pauseBtn.setOnClickListener {
            val fadeIn: Animation = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator()
            fadeIn.duration = 500
            val animation = AnimationSet(false)
            animation.addAnimation(fadeIn)
            playBtn.animation = animation
            pauseBtn.visibility = View.GONE
            playBtn.visibility = View.VISIBLE
            videoView.pause()
        }

        fullscreen.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Handler().postDelayed(
                { requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR },
                Constant.TIME_OUT
            )
        }

        fullscreenExit.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Handler().postDelayed(
                { requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR },
                Constant.TIME_OUT
            )
        }
    }

    private fun handleCommentSection() {

        firstCommentSection.setOnClickListener {
            toggleCommentSection(true)
        }

        videoInfoView.setOnClickListener {
            toggleCommentSection(false)
        }

        binding.closeCommentList.setOnClickListener{
            toggleCommentSection(false)
        }
    }

    private fun handleSendComment() {
        binding.commentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                binding.btnSendComment.visibility = View.VISIBLE

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count == 0)
                    binding.btnSendComment.visibility = View.GONE
            }
        })

        binding.btnSendComment.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()
            binding.commentEditText.text.clear()

            val toast = Toast.makeText(this, commentText, Toast.LENGTH_SHORT)
            toast.show()

            binding.btnSendComment.visibility = View.GONE

            applicationContext?.let { it1 -> hideKeyboardFrom(
                it1,
                binding.commentEditText
            ) }

        }
    }

    private fun setClickListeners() {

        handleVideoPlayer()

        handleCommentSection()

        handleSendComment()

//        dismissControlFrame.setOnClickListener {
//            dismissControls()
//        }

//        showImgUp.setOnClickListener{
//            showUp()
//        }
//        showImgDown.setOnClickListener{
//            showDown()
//        }
    }

    private fun toggleCommentSection(show: Boolean) {
        if ((!show && commentListView.visibility == View.VISIBLE) ||
            show && commentListView.visibility == View.GONE) {
            val transition: Transition = Slide(Gravity.BOTTOM)
            transition.duration = 600
            transition.addTarget(R.id.comment_list)
            TransitionManager.beginDelayedTransition(videoInfoView, transition)
            commentListView.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun initVideo() {
        Timber.e("Ngan %s", "video's url: " + currentVideo?.url)
        videoView.setVideoURI(Uri.parse(currentVideo?.url ?: ""))
        if (videoView.isPlaying)
            progressBar.visibility = View.VISIBLE

        videoView.setOnPreparedListener { mp ->
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
            mp.setOnBufferingUpdateListener { _, percent -> bufferBar.progress = percent }
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
                val decorView: View? = window?.decorView
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
        val decorView: View? = window?.decorView
        if (decorView != null) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun animForward(view: ImageView) {
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

    private fun prepareAnimationForward(animation: Animation): Animation {
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

    private fun prepareAnimationBackward(animation: Animation): Animation {
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

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }

    private fun setHandler() {
        videoHandler = Handler()
        videoRunnable = object : Runnable {
            override fun run() {
                if (videoView.duration > 0) {
                    val currentPosition = videoView.currentPosition
                    seekBar.progress = currentPosition
                    startTime.text = "" + convertIntToTime(currentPosition)
                    endTime.text = "" + convertIntToTime(videoView.duration)
                }
                videoHandler.postDelayed(this, 0)
            }
        }
        videoHandler.postDelayed(videoRunnable, 500)
    }

    private fun convertIntToTime(ms: Int): String {
        val seconds: Int
        val minutes: Int
        val hours: Int
        var x: Int = ms / 1000
        seconds = x % 60
        x /= 60
        minutes = x % 60
        x /= 60
        hours = x % 24
        return if (hours != 0) String.format("%02d", hours) + ":" + String.format(
            "%02d",
            minutes
        ) + ":" + String.format("%02d", seconds) else String.format(
            "%02d",
            minutes
        ) + ":" + String.format("%02d", seconds)
    }

    private fun initialiseSeekBar() {
        seekBar.progress = 0
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

    override fun onBackPressed() {
        releaseVideoPlayer()
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

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}