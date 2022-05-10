package com.wanotube.wanotubeapp.ui.watch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.domain.Comment

class WatchAdapter : RecyclerView.Adapter<WatchAdapter.ViewHolder>() {

    var comments =  listOf<Comment>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = comments[position]
        holder.bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val commentContentView: TextView = itemView.findViewById(R.id.comment_content)
        private val authorNameView: TextView = itemView.findViewById(R.id.author_name)

        private val avatarView: ImageView = itemView.findViewById(R.id.avatar)

        fun bind(item: Comment) {
            commentContentView.text = item.content
            authorNameView.text = item.authorUsername
            
//            Glide.with(avatarView.context)
//                .load(item.thumbnail)
//                .circleCrop()
//                .into(avatarView)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.comment_component_list, parent, false)
                return ViewHolder(view)
            }
        }
    }
}