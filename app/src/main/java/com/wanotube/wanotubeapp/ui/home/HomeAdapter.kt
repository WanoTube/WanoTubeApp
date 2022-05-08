package com.wanotube.wanotubeapp.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.IEventListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.repository.ChannelRepository
import com.wanotube.wanotubeapp.ui.watch.WatchActivity

class HomeAdapter(iEventListener: IEventListener) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var listener: IEventListener = iEventListener

    var data =  listOf<Video>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, listener)
    }

    class ViewHolder private constructor(itemView: View, listener: IEventListener) : RecyclerView.ViewHolder(
        itemView
    ){

        private var listener: IEventListener = listener
        private lateinit var channelRepository : ChannelRepository
        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitle)

        private val thumbnailVideoView: ImageView = itemView.findViewById(R.id.thumbnail_video)
        private val avatarView: ImageView = itemView.findViewById(R.id.avatar_user)

        fun bind(item: Video) {
            val context = thumbnailVideoView.context

            if (!::channelRepository.isInitialized) {
                channelRepository = ChannelRepository(getDatabase(context))
            }
            
            val channel = channelRepository.channels.value?.find {
                it.userId == item.authorId
            }
            if (channel != null) {
                val subtitle = channel.username + "  " + item.totalViews + " views"
                subtitleView.text = subtitle
            }
            titleView.text = item.title
            
            Glide.with(thumbnailVideoView.context)
                .load(item.thumbnail)
                .override(480, 269)
                .centerCrop()
                .into(thumbnailVideoView)
            Glide.with(avatarView.context)
                .load(item.thumbnail)
                .circleCrop()
                .into(avatarView)

            thumbnailVideoView.setOnClickListener{
                val intent = Intent(context, WatchActivity::class.java)
                intent.putExtra("VIDEO_ID", item.id)
                context.startActivity(intent)
            }
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