package com.wanotube.wanotubeapp.ui.library.following

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.entity.DatabaseChannel

class FollowingAdapter : RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    var data =  listOf<DatabaseChannel>()
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

        private val authorNameView: TextView = itemView.findViewById(R.id.author_name)
        private val authorFollowersView: TextView = itemView.findViewById(R.id.content)

        private val avatarView: ImageView = itemView.findViewById(R.id.avatar)

        fun bind(item: DatabaseChannel) {
            authorNameView.text = item.username
            authorFollowersView.text = item.numberOfFollowers.toString() + " followers"

            Glide.with(avatarView.context)
                .load(item.avatar)
                .circleCrop()
                .into(avatarView)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.channel_component_list, parent, false)
                return ViewHolder(view)
            }
        }
    }
}