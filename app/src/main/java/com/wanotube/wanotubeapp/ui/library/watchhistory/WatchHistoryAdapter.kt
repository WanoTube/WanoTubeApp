package com.wanotube.wanotubeapp.ui.library.watchhistory

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.entity.DatabaseWatchHistory
class WatchHistoryAdapter(val application: Application) : RecyclerView.Adapter<WatchHistoryAdapter.ViewHolder>() {

    var data =  listOf<DatabaseWatchHistory>()
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
        private val dateView: TextView = itemView.findViewById(R.id.history_date)
        private val videoListView: RecyclerView = itemView.findViewById(R.id.watch_history_list_by_date)
        
        fun bind(item: DatabaseWatchHistory) {
            dateView.text = item.date
            val adapter = VideoHistoryAdapter(application)
            videoListView.adapter = adapter
            adapter.data = item.videos
        }

        companion object {
            fun from(application: Application, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.watch_history_component_list, parent, false)
                return ViewHolder(application, view)
            }
        }
    }
}