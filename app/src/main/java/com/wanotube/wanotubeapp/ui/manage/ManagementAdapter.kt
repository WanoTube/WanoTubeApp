package com.wanotube.wanotubeapp.ui.manage

import android.annotation.SuppressLint
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
import com.wanotube.wanotubeapp.IEventListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.ui.edit.EditInfoActivity
import com.wanotube.wanotubeapp.ui.watch.WatchActivity

class ManagementAdapter(iEventListener: IEventListener) : RecyclerView.Adapter<ManagementAdapter.ViewHolder>() {

    private var listener: IEventListener = iEventListener

    var data =  listOf<Video>()
        @SuppressLint("NotifyDataSetChanged")
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

    class ViewHolder private constructor(itemView: View, var listener: IEventListener) : RecyclerView.ViewHolder(
        itemView
    ){

        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitle)
        private val thumbnailVideoView: ImageView = itemView.findViewById(R.id.thumbnail_video)
        private val menuView: ImageView = itemView.findViewById(R.id.video_menu)
        
        fun bind(item: Video) {
            titleView.text = item.title
            val subtitle = item.authorId + "  " + item.totalViews + " views"
            subtitleView.text = subtitle

            Glide.with(thumbnailVideoView.context)
                .load(item.thumbnail)
                .override(480, 269)
                .centerCrop()
                .into(thumbnailVideoView)

            val context = thumbnailVideoView.context
            thumbnailVideoView.setOnClickListener{
                val intent = Intent(context, WatchActivity::class.java)
                intent.putExtra("VIDEO_ID", item.id)
                context.startActivity(intent)
            }
            menuView.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(context)
                bottomSheetDialog.apply {
                    setContentView(R.layout.video_menu_dialog)
                    findViewById<LinearLayout>(R.id.edit_video)?.setOnClickListener {
                        val intent = Intent(context, EditInfoActivity::class.java)
                        intent.putExtra("VIDEO_ID", item.id)
                        context.startActivity(intent)
                    }
                    findViewById<LinearLayout>(R.id.delete_video)?.setOnClickListener {
                    }
                    show()
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup, listener: IEventListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.management_video_component_list, parent, false)
                return ViewHolder(view, listener)
            }
        }
    }
}