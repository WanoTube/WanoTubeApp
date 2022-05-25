package com.wanotube.wanotubeapp.ui.shorts

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.like.LikeButton
import com.like.OnLikeListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanotubeApp.Companion.context
import com.wanotube.wanotubeapp.components.videoPlayer.PlayerStateCallback
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.databinding.ShortVideoComponentListBinding
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.ui.components.comments.CommentDialogFragment
import com.wanotube.wanotubeapp.ui.login.LoginActivity
import com.wanotube.wanotubeapp.util.Constant
import timber.log.Timber


class ShortAdapter : RecyclerView.Adapter<ShortAdapter.VideoViewHolder>(), PlayerStateCallback {

    var data =  listOf<Video>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
//    override fun onViewRecycled(holder: VideoViewHolder) {
//        val videoView = holder.binding.videoView
//        releasePlayer(videoView)
//        super.onViewRecycled(holder)
//    }
//
//
//    private fun releasePlayer(videoView: PlayerView) {
//        videoView.player?.run {
//            release()
//        }
//        videoView.player = null
//    }
    
    override fun onViewAttachedToWindow(holder: VideoViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.videoView.player?.pause()
        with(holder.binding) {
            isPlaying = true
        }

    }

    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.videoView.player?.pause()
        with(holder.binding) {
            isPlaying = false
            Timber.e("onViewDetachedFromWindow, isPlaying: $isPlaying")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = DataBindingUtil.inflate<ShortVideoComponentListBinding>(
            LayoutInflater.from(parent.context), R.layout.short_video_component_list, parent, false
        )
        return VideoViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(viewHolder: VideoViewHolder, position: Int) {
        val item = data[position]
        with(viewHolder.binding) {
            url = item.url
            callback = this@ShortAdapter
            isPlaying = false
            executePendingBindings()

            exoPlay.setOnClickListener {
                startPlayer(viewHolder.binding)
            }
            
            videoView.setOnClickListener {
                if (videoView.player?.isPlaying == true) {
                    pausePlayer(viewHolder.binding)
                } else {
                    startPlayer(viewHolder.binding)
                }
            }
        }
        viewHolder.setData(item)
    }

    private fun pausePlayer(binding: ShortVideoComponentListBinding) {
        binding.videoView.player!!.playWhenReady = false
        binding.exoPlay.visibility = View.VISIBLE
        binding.exoPause.visibility = View.GONE
    }

    private fun startPlayer(binding: ShortVideoComponentListBinding) {
        binding.videoView.player!!.playWhenReady = true
        binding.exoPlay.visibility = View.GONE
    }

    class VideoViewHolder constructor(val binding: ShortVideoComponentListBinding) : RecyclerView.ViewHolder(binding.root) {

        var title: TextView = itemView.findViewById<View>(R.id.textVideoTitle) as TextView
        var desc: TextView = itemView.findViewById<View>(R.id.textVideoDescription) as TextView
//        var pbar: ProgressBar = itemView.findViewById<View>(R.id.videoProgressBar) as ProgressBar
        var likeButton: LikeButton = itemView.findViewById(R.id.like_button) as LikeButton
        var shareButton: ImageView = itemView.findViewById(R.id.share_button) as ImageView
        var commentButton: ImageView = itemView.findViewById(R.id.comment_button) as ImageView
        var totalLikesTextView :TextView = itemView.findViewById(R.id.total_likes) as TextView

        val videosRepository = context?.let { getDatabase(it) }?.let { VideosRepository(it) }
        val mAuthPreferences = context?.let { AuthPreferences(it) }
        
        var bottomSheetDialog: CommentDialogFragment? = null

        fun setData(obj: Video) {

            title.text = obj.title
            desc.text = obj.description

            val context = title.context

            handleLike(context, obj)
            handleShare(context, obj)
            handleComment(context, obj)

            binding.totalLikes.text = obj.totalLikes.toString()
            binding.totalComments.text = obj.totalComments.toString()

            bottomSheetDialog = CommentDialogFragment.createInstance(obj.id)
        }

        private fun handleComment(context: Context, obj: Video) {
            commentButton.setOnClickListener {
                showBottomSheetDialog()
            }
        }

        private fun showBottomSheetDialog() {

            val fragmentManager: FragmentManager =
                (itemView.context as FragmentActivity).supportFragmentManager // instantiate your view context

            bottomSheetDialog?.show(fragmentManager, bottomSheetDialog!!.tag)
        }

        private fun handleShare(context: Context, obj: Video) {
            shareButton.setOnClickListener {
                ShareCompat.IntentBuilder(context)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText("${Constant.PRODUCTION_WEB_URL}/watch/${obj.id}")
                    .startChooser()
            }
        }

        private fun checkTokenAvailable(openLoginActivity: Boolean = true): Boolean {
            val email =  mAuthPreferences!!.email
            val account = AccountUtils.getAccount(context, email)
            val token = mAuthPreferences!!.authToken
            val result = account != null && token != null
            if (!result && openLoginActivity) {
                openLoginActivity()
            }
            return result
        }

        private fun openLoginActivity() {
            val intent = Intent(binding.root.context, LoginActivity::class.java)
            binding.root.context.startActivity(intent)
        }

        private fun handleLike(context: Context, obj: Video) {
            likeButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {
                    if (checkTokenAvailable()) {
                        videosRepository?.likeVideo(obj.id)
                    }
                }
                override fun unLiked(likeButton: LikeButton) {
                    if (checkTokenAvailable()) {
                        videosRepository?.likeVideo(obj.id)
                    }
                }
            })
        }
    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {
        
    }

    override fun onVideoBuffering(player: Player) {
        
    }

    override fun onStartedPlaying(player: Player) {
        
    }

    override fun onFinishedPlaying(player: Player) {
        
    }

}
