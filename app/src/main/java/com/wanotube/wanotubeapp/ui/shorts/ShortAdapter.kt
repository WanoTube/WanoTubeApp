package com.wanotube.wanotubeapp.ui.shorts

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.RecyclerView
import com.like.LikeButton
import com.like.OnLikeListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanotubeApp.Companion.context
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.ui.login.LoginActivity
import com.wanotube.wanotubeapp.util.Constant

class ShortAdapter() : RecyclerView.Adapter<ShortAdapter.ViewHolder>() {

    var data =  listOf<Video>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var videoView: VideoView = itemView.findViewById<View>(R.id.videoView) as VideoView
        var title: TextView = itemView.findViewById<View>(R.id.textVideoTitle) as TextView
        var desc: TextView = itemView.findViewById<View>(R.id.textVideoDescription) as TextView
        var pbar: ProgressBar = itemView.findViewById<View>(R.id.videoProgressBar) as ProgressBar
        var likeButton: LikeButton = itemView.findViewById(R.id.favorites) as LikeButton
        var shareButton: ImageView = itemView.findViewById(R.id.share_button) as ImageView
        var commentButton: ImageView = itemView.findViewById(R.id.comment_button) as ImageView
        var mediaPlayer: MediaPlayer? = null
        var totalLikesTextView :TextView = itemView.findViewById(R.id.total_likes) as TextView

        val videosRepository = context?.let { getDatabase(it) }?.let { VideosRepository(it) }
        val mAuthPreferences = context?.let { AuthPreferences(it) }

        fun setData(obj: Video) {
            videoView.setVideoPath(obj.url)
            title.text = obj.title
            desc.text = obj.description
            videoView.setOnPreparedListener { mediaPlayer ->
                this.mediaPlayer = mediaPlayer
                pbar.visibility = View.GONE
                mediaPlayer.start()
            }
            videoView.setOnCompletionListener { mediaPlayer -> mediaPlayer.start() }
            videoView.setOnClickListener {
                if (mediaPlayer?.isPlaying == true) {
                    this.mediaPlayer?.pause()
                } else {
                    this.mediaPlayer?.start()
                }
            }
            val context = videoView.context

            handleLike(context, obj)
            handleShare(context, obj)
            handleComment(obj)
        }

        private fun handleComment(obj: Video) {
            commentButton.setOnClickListener { 
                
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
        
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.short_video_component_list, parent, false)
                return ViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.setData(item)
    }
}
