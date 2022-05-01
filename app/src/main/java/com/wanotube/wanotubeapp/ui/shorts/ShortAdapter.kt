package com.wanotube.wanotubeapp.ui.shorts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.domain.WanoTubeVideo

class ShortAdapter() : RecyclerView.Adapter<ShortAdapter.ViewHolder>() {

    var data =  listOf<WanoTubeVideo>()
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
        var fav :ImageView = itemView.findViewById(R.id.favorites) as ImageView
        var isFav = false
        fun setData(obj: WanoTubeVideo) {
            videoView.setVideoPath(obj.url)
            title.text = obj.title
            desc.text = obj.description
            videoView.setOnPreparedListener { mediaPlayer ->
                pbar.visibility = View.GONE
                mediaPlayer.start()
            }
            videoView.setOnCompletionListener { mediaPlayer -> mediaPlayer.start() }
            fav.setOnClickListener {
                if (!isFav){
                    fav.setImageResource(R.drawable.ic_fill_favorite)
                    isFav = true
                }
                else{
                    fav.setImageResource(R.drawable.ic_favorite)
                    isFav = false
                }

            }
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
