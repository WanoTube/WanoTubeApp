package com.wanotube.wanotubeapp.carousel

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R

class CarouselAdapter: RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {

    var itemList =  listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){

        var circleButton = itemView.findViewById<ImageButton>(R.id.circle_button)
        fun bind(item: String) {
            val context = itemView.context
            val resID: Int = context.resources.getIdentifier(item, "drawable", context.packageName)

            Glide.with(circleButton.context)
                .load(if (resID == 0 || item == "none") {
                    R.drawable.record_selector
                } else {
                    resID
                })
                .placeholder(R.drawable.image_placeholder)
                .circleCrop()
                .into(circleButton)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.circle_button, parent, false)
                return ViewHolder(view)
            }
        }
    }
}