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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import com.wanotube.wanotubeapp.ui.login.LoginActivity
import com.wanotube.wanotubeapp.util.Constant

class ShortAdapter : RecyclerView.Adapter<ShortAdapter.VideoViewHolder>(), PlayerStateCallback {

    var data =  listOf<Video>()
        set(value) {
            field = value
            notifyDataSetChanged()
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
            executePendingBindings()
        }
        viewHolder.setData(item)
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

        fun setData(obj: Video) {

            title.text = obj.title
            desc.text = obj.description

            val context = title.context

            handleLike(context, obj)
            handleShare(context, obj)
            handleComment(context, obj)
        }

        private fun handleComment(context: Context, obj: Video) {
            commentButton.setOnClickListener {
                showBottomSheetDialog(context)
            }
        }

        private fun showBottomSheetDialog(context: Context) {
            val bottomSheetDialog = BottomSheetDialog(context)
            bottomSheetDialog.apply {
                setContentView(R.layout.dialog_comment_short)
                show()
            }
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
            val intent = Intent(context, LoginActivity::class.java)
            context?.startActivity(intent)
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

//            val observeOwner = context
//            CoroutineScope(Dispatchers.IO).launch {
//                val video = videosRepository?.getVideoFromDatabase(obj.id)
//                withContext(Dispatchers.Main) {
//                    video?.observe(observeOwner) { video ->
//                        if (video != null) {
//                            totalLikesTextView.text = video.totalLikes.toString()
//                        }
//                    }
//                }
//            }
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
