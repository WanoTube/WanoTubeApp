package com.wanotube.wanotubeapp.ui.watch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.domain.Comment

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    var comments =  listOf<Comment>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var channels = listOf<Account>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = comments[position]
        holder.bind(item, channels)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, channels)
    }

    class ViewHolder private constructor(itemView: View, private val channels: List<Account>) : RecyclerView.ViewHolder(itemView){

        private val commentContentView: TextView = itemView.findViewById(R.id.content)
        private val authorNameView: TextView = itemView.findViewById(R.id.author_name)

        private val avatarView: ImageView = itemView.findViewById(R.id.avatar)

        fun bind(item: Comment, channels: List<Account>) {
            commentContentView.text = item.content
            authorNameView.text = item.authorUsername

            val channel = channels.find {
                it.id == item.authorId
            }

            if (channel?.avatar != null) {
                Glide.with(avatarView.context)
                    .load(channel.avatar)
                    .placeholder(R.drawable.image_placeholder)
                    .circleCrop()
                    .into(avatarView)
            }
        }

        companion object {
            fun from(parent: ViewGroup, channels: List<Account>): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.channel_component_list, parent, false)
                return ViewHolder(view, channels)
            }
        }
    }
}