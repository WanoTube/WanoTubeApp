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
import com.wanotube.wanotubeapp.domain.VideoModel
import com.wanotube.wanotubeapp.domain.WanoTubeVideo
import com.wanotube.wanotubeapp.ui.home.HomeAdapter

//class ShortAdapter() : RecyclerView.Adapter<ShortAdapter.ViewHolder>() {
//
//    var data =  listOf<WanoTubeVideo>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
//        val item = data[position]
//        holder.bind(item)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_shorts, parent, false)
//        return ViewHolder(view)
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var videoView: VideoView = itemView.findViewById<View>(R.id.videoView) as VideoView
//        var title: TextView = itemView.findViewById<View>(R.id.textVideoTitle) as TextView
//        var desc: TextView = itemView.findViewById<View>(R.id.textVideoDescription) as TextView
//        var pbar: ProgressBar = itemView.findViewById<View>(R.id.videoProgressBar) as ProgressBar
//        var fav :ImageView = itemView.findViewById(R.id.favorites) as ImageView
//        var isFav = false
//        fun setData(obj: VideoModel) {
//            videoView.setVideoPath(obj.url)
//            title.text = obj.title
//            desc.text = obj.desc
//            videoView.setOnPreparedListener { mediaPlayer ->
//                pbar.visibility = View.GONE
//                mediaPlayer.start()
//            }
//            videoView.setOnCompletionListener { mediaPlayer -> mediaPlayer.start() }
//            fav.setOnClickListener {
//                if (!isFav){
//                    fav.setImageResource(R.drawable.ic_fill_favorite)
//                    isFav = true
//                }
//                else{
//                    fav.setImageResource(R.drawable.ic_favorite)
//                    isFav = false
//                }
//
//            }
//        }
//
//    }
//
//    override fun getItemCount(): Int {
//        TODO("Not yet implemented")
//    }
//}
