package com.wanotube.wanotubeapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.domain.WanoTubeVideo
import com.wanotube.wanotubeapp.util.getThumbnailYoutubeVideo

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    var data =  listOf<WanoTubeVideo>()
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
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitle)

        private val thumbnailVideoView: ImageView = itemView.findViewById(R.id.thumbnail_video)
        private val avatarView: ImageView = itemView.findViewById(R.id.avatar_user)

        fun bind(item: WanoTubeVideo) {
            titleView.text = item.title
            val subtitle = item.authorId + "  " + item.totalViews + "views"
            subtitleView.text = subtitle

            val thumbnailVideo = getThumbnailYoutubeVideo(item.url)
            Glide.with(thumbnailVideoView.context)
                .load(thumbnailVideo)
                .override(480, 269)
                .centerCrop()
                .into(thumbnailVideoView)
            Glide.with(avatarView.context)
                .load(thumbnailVideo)
                .circleCrop()
                .into(avatarView)

            thumbnailVideoView.setOnClickListener{ view ->
                view.findNavController().navigate(R.id.fragment_watch)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.home_video_component_list, parent, false)
                return ViewHolder(view)
            }
        }
    }
}

class VideoDiffCallback : DiffUtil.ItemCallback<WanoTubeVideo>() {
    override fun areItemsTheSame(oldItem: WanoTubeVideo, newItem: WanoTubeVideo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WanoTubeVideo, newItem: WanoTubeVideo): Boolean {
        return oldItem == newItem
    }
}