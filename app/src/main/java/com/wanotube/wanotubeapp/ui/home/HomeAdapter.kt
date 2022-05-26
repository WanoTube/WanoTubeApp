package com.wanotube.wanotubeapp.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wanotube.wanotubeapp.IEventListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.ui.watch.WatchActivity
import com.wanotube.wanotubeapp.util.toTimeAgo

class HomeAdapter(iEventListener: IEventListener) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var listener: IEventListener = iEventListener

    var data =  listOf<Video>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    
    var channels = listOf<Account>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, channels)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, listener)
    }

    class ViewHolder private constructor(itemView: View, listener: IEventListener) : RecyclerView.ViewHolder(
        itemView
    ){

        private var listener: IEventListener = listener
        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitle)
        private val thumbnailVideoView: ImageView = itemView.findViewById(R.id.thumbnail_video)
        private val avatarView: ImageView = itemView.findViewById(R.id.avatar_user)
        private val menuView: TextView = itemView.findViewById(R.id.video_menu)

        fun bind(item: Video, channels: List<Account>) {
            val context = thumbnailVideoView.context

            val channel = channels.find {
                it.userId == item.authorId
            }
            if (channel != null) {
                val subtitle = channel.username + "  " + 
                        item.totalViews + " views  " + 
                        item.createdAt.time.toTimeAgo()
                subtitleView.text = subtitle

                Glide.with(avatarView.context)
                    .load(channel.avatar)
                    .circleCrop()
                    .into(avatarView)
            }
            titleView.text = item.title
            
            Glide.with(thumbnailVideoView.context)
                .load(item.thumbnail)
                .override(480, 269)
                .centerCrop()
                .into(thumbnailVideoView)

            thumbnailVideoView.setOnClickListener{
                val intent = Intent(context, WatchActivity::class.java)
                intent.putExtra("VIDEO_ID", item.id)
                intent.putExtra("NEED_TOKEN", false)
                context.startActivity(intent)
            }

            menuView.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(context)
                bottomSheetDialog.apply {
                    setContentView(R.layout.home_video_menu_dialog)
                    findViewById<LinearLayout>(R.id.save_watch_later)?.setOnClickListener {
                        saveToWatchLater()
                    }
                    findViewById<LinearLayout>(R.id.share)?.setOnClickListener {
                    }
                    show()
                }
            }
        }
        
        private fun saveToWatchLater() {
            
        }

        companion object {
            fun from(parent: ViewGroup, listener: IEventListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.home_video_component_list, parent, false)
                return ViewHolder(view, listener)
            }
        }
    }
}

class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem == newItem
    }
}