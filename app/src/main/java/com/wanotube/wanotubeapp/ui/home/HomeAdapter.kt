package com.wanotube.wanotubeapp.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.DatabaseVideo
import com.wanotube.wanotubeapp.domain.WanoTubeVideo

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

        val textView: TextView = itemView.findViewById(R.id.sleep_length)

        fun bind(item: WanoTubeVideo) {
//            val res = itemView.context.resources
            textView.text = item.description
            Log.e("Ngan", item.description)
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