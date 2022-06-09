package com.wanotube.wanotubeapp.ui.watch

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ShareCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.like.LikeButton
import com.like.OnLikeListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.WanotubeApp
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.databinding.ActivityNewWatchBinding
import com.wanotube.wanotubeapp.domain.User
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.network.objects.NetworkVideoWatch
import com.wanotube.wanotubeapp.repository.ChannelRepository
import com.wanotube.wanotubeapp.repository.CommentRepository
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.util.Constant.PRODUCTION_WEB_URL
import com.wanotube.wanotubeapp.util.isCountedAsView
import com.wanotube.wanotubeapp.viewmodels.CommentViewModel
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class NewWatchActivity : WanoTubeActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityNewWatchBinding.inflate(layoutInflater)
    }
    
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private lateinit var videoLayout: RelativeLayout
    private lateinit var firstCommentSection: LinearLayout
    private lateinit var commentListView: ScrollView
    private lateinit var videoInfoView: ScrollView

    private lateinit var videosRepository: VideosRepository
    private lateinit var commentRepository: CommentRepository
    private lateinit var channelRepository: ChannelRepository

    private lateinit var adapter: CommentAdapter

    private lateinit var currentVideo: Video
    private var currentUser: User? = null

    private var channelId = ""
    private var username = ""
    private var videoId = ""

    private var isVideoInsertedToDB = false
    private var isIncreasedView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videosRepository = VideosRepository(getDatabase(application))
        commentRepository = CommentRepository(getDatabase(application))
        channelRepository = ChannelRepository(getDatabase(application))

        setContentView(binding.root)

        supportActionBar?.hide()

        initLayouts(binding)
//        initialiseSeekBar()
        initAdapter()
        getVideo()
        setClickListeners()
        loadCommentAuthorAvatar()
    }

    private fun loadCommentAuthorAvatar() {
        val avatarCommentAuthor = mAuthPreferences?.avatar
        if (avatarCommentAuthor != null) {
            loadAvatar(avatarCommentAuthor, binding.avatarCommentAuthor)
        }
    }

    private fun initAdapter() {
        videoId = intent.getStringExtra("VIDEO_ID").toString()

        val viewModelFactory = CommentViewModel.CommentViewModelFactory(application)

        val commentViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(CommentViewModel::class.java)

        binding.commentViewModel = commentViewModel

        adapter = CommentAdapter()

        binding.allComments.adapter = adapter

        binding.lifecycleOwner = this

        val videoViewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        val videoViewModel =
            ViewModelProvider(
                this, videoViewModelFactory
            ).get(WanoTubeViewModel::class.java)

        videoViewModel.channels.observe(this) {
            it?.let {
                adapter.channels = it
            }
        }
        commentViewModel.comments.observe(this) {
            it?.let {
                val videos = it.filter {
                        video ->  video.videoId == videoId
                }
                adapter.comments = videos
                binding.commentTotal.text = adapter.itemCount.toString()
                binding.totalComments.text = adapter.itemCount.toString()
            }
        }
    }

    private fun getVideo() {
        CoroutineScope(Dispatchers.IO).launch {
            val doesNeedToken = intent.getBooleanExtra("NEED_TOKEN", false)
            val responseBodyCall = if (doesNeedToken) {
                videosRepository.getVideoWithAuthorization(videoId)
            } else {
                videosRepository.getVideo(videoId)
            }

            responseBodyCall?.enqueue(object : Callback<NetworkVideoWatch> {
                override fun onResponse(
                    call: Call<NetworkVideoWatch>?,
                    response: Response<NetworkVideoWatch?>?,
                ) {
                    if (response != null) {
                        if (response.code() == 200) {
                            val videoDatabase = response.body()?.asDatabaseModel()
                            currentVideo = videoDatabase?.asDomainModel()!!
                            binding.totalLikes.text = videoDatabase.totalLikes.toString()

                            if (!isVideoInsertedToDB) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    videosRepository.insertVideoToDatabase(videoDatabase)
                                }
                                isVideoInsertedToDB = true
                            }
                            
                            if (Util.SDK_INT >= 24) {
                                initializePlayer()
                            }
                            
                            channelId = response.body()?.user?.channelId.toString()
                            username = response.body()?.user?.username.toString()
                            currentUser = response.body()?.user?.doc?.user?.asDatabaseModel()?.asDomainModel()
                            val authorAvatar = response.body()?.user?.avatar.toString()
                            loadAvatar(authorAvatar, binding.avatarAuthor)
                            initVideo()
                        } else {
                            Toast.makeText(WanotubeApp.context, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<NetworkVideoWatch>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }

    private fun loadAvatar(avatarUrl: String, avatarView: ImageView) {
        Glide.with(avatarView.context)
            .load(avatarUrl)
            .placeholder(R.drawable.image_placeholder)
            .circleCrop()
            .into(avatarView)
    }

    private fun initLayouts(binding: ActivityNewWatchBinding) {
        videoLayout = binding.videoLayout
        firstCommentSection = binding.firstComment
        commentListView = binding.commentList
        videoInfoView = binding.videoInfoSection
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
            if (checkTokenAvailable()) {
                val commentText = binding.commentEditText.text.toString()
                binding.commentEditText.text.clear()
                currentVideo.id.let { videoId ->
                    commentRepository.sendComment(commentText, videoId, adapter)
                }

                binding.btnSendComment.visibility = View.GONE

                applicationContext?.let { it1 -> hideKeyboardFrom(
                    it1,
                    binding.commentEditText
                ) }

            }
        }
    }

    private fun setClickListeners() {
        handleSendComment()
        handleLike()
        handleShare()
        handleFollow()
    }

    private fun handleFollow() {
        binding.follow.setOnClickListener {
            if (checkTokenAvailable()) {

                CoroutineScope(Dispatchers.IO).launch {
                    if (binding.follow.text == "FOLLOW") {
                        channelRepository.followChannel(channelId)
                        withContext(Dispatchers.Main) {
                            binding.follow.text = "UNFOLLOW"
                            binding.follow.setBackgroundColor(Color.WHITE)
                            binding.follow.setTextColor(Color.BLACK)
                        }

                    } else {
                        channelRepository.unfollowChannel(channelId)
                        withContext(Dispatchers.Main) {
                            binding.follow.text = "FOLLOW"
                            binding.follow.setBackgroundColor(resources.getColor(R.color.dark_pink))
                            binding.follow.setTextColor(Color.WHITE)
                        }
                    }
                }
            }
        }
    }

    private fun handleShare() {
        binding.shareButton.setOnClickListener {
            ShareCompat.IntentBuilder(this)
                .setType("text/plain")
                .setChooserTitle("Share URL")
                .setText("$PRODUCTION_WEB_URL/watch/$videoId")
                .startChooser()
        }
    }

    private fun handleLike() {
        binding.likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                if (checkTokenAvailable()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        videosRepository.likeVideo(videoId)
                    }
                }
                binding.totalLikes.text = (currentVideo.totalLikes.plus(1)).toString()
            }
            override fun unLiked(likeButton: LikeButton) {
                if (checkTokenAvailable()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        videosRepository.likeVideo(videoId)
                    }
                    //TODO: NOT HARDCODED
//                    binding.totalLikes.text = (currentVideo?.totalLikes?.minus(1)).toString()
                }
            }
        })

        val observeOwner = this
        CoroutineScope(Dispatchers.IO).launch {
            val video = videosRepository.getVideoFromDatabase(videoId)
            withContext(Dispatchers.Main) {
                video?.observe(observeOwner) { video ->
                    if (video != null) {
                        if (isVideoInsertedToDB) {
                            binding.totalLikes.text = video.totalLikes.toString()
                        }
                    } else {
                        isVideoInsertedToDB = false
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initVideo() {
        binding.title.text = currentVideo.title
        binding.subtitle.text = currentVideo.totalViews.toString() + " views"
        binding.authorName.text = username
        binding.totalLikes.text = currentVideo.totalLikes.toString()
        binding.totalComments.text = currentVideo.totalComments.toString()
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
        binding.videoView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
    }

    private fun initializePlayer() {
        if (!::currentVideo.isInitialized)
            return
//        val trackSelector = DefaultTrackSelector(this).apply {
//            setParameters(buildUponParameters().setMaxVideoSizeSd())
//        }
        player = SimpleExoPlayer.Builder(this)
//            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(currentVideo.url)
                exoPlayer.setMediaItem(mediaItem)

                //playWhenReady tells the player whether to start playing as soon as all resources for playback have been acquired. Because playWhenReady is initially true, playback starts automatically the first time the app is run.
                exoPlayer.playWhenReady = playWhenReady
                //seekTo tells the player to seek to a certain position within a specific window.
                //Both currentWindow and playbackPosition are initialized to zero so that playback starts from the very start the first time the app is run.
                exoPlayer.seekTo(currentWindow, playbackPosition)
                //prepare tells the player to acquire all the resources required for playback.
                exoPlayer.prepare()
            }
        player!!.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                updateProgressBar()
            }
        })
    }

    private fun updateProgressBar() {
        if (player?.currentPosition?.let { player?.duration?.let { it1 -> isCountedAsView(it, it1) } } == true) {
            increaseView()
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


    private fun increaseView() {
        if (!isIncreasedView) {
            isIncreasedView = true
            videosRepository.increaseView(videoId)
        }
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}