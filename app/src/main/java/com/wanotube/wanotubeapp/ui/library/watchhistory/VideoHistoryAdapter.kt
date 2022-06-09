package com.wanotube.wanotubeapp.ui.library.watchhistory

import android.app.Application
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.ui.edit.EditInfoActivity
import com.wanotube.wanotubeapp.ui.watch.WatchActivity
import com.wanotube.wanotubeapp.util.toTimeAgo

class VideoHistoryAdapter(val application: Application) : RecyclerView.Adapter<VideoHistoryAdapter.ViewHolder>() {

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
        return ViewHolder.from(application, parent)
    }

    class ViewHolder private constructor(val application: Application, itemView: View) : RecyclerView.ViewHolder(
        itemView
    ){

        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitle)
        private val thumbnailVideoView: ImageView = itemView.findViewById(R.id.thumbnail_video)
        private val menuView: ImageView = itemView.findViewById(R.id.video_menu)

        fun bind(item: Video) {
            val context = thumbnailVideoView.context

            titleView.text = item.title

            //TODO: Not hardcode
            item.thumbnail = "https://d1td7i7nd90xol.cloudfront.net/son.png"
            Glide.with(thumbnailVideoView.context)
                .load(item.thumbnail)
                .placeholder(R.drawable.video_placeholder)
                .override(480, 269)
                .centerCrop()
                .into(thumbnailVideoView)

            //TODO
//            thumbnailVideoView.setOnClickListener{
//                val intent = Intent(context, WatchActivity::class.java)
//                intent.putExtra("VIDEO_ID", item.id)
//                intent.putExtra("NEED_TOKEN", false)
//                context.startActivity(intent)
//            }

            menuView.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(context)
                bottomSheetDialog.apply {
                    setContentView(R.layout.watch_later_menu_dialog)
                    findViewById<LinearLayout>(R.id.remove_watch_later)?.setOnClickListener {
                        removeFromWatchLater(item.id)
                        bottomSheetDialog.hide()
                    }
                    findViewById<LinearLayout>(R.id.share)?.setOnClickListener {
                    }
                    show()
                }
            }
        }

        private fun removeFromWatchLater(videoId: String) {
            val videosRepository = VideosRepository(getDatabase(application))
            videosRepository.removeWatchLater(videoId)
        }

        companion object {
            fun from(application: Application, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.management_video_component_list, parent, false)
                return ViewHolder(application, view)
            }
        }
    }
}