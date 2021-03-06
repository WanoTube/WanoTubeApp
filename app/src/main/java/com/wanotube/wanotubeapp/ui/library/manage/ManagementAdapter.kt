package com.wanotube.wanotubeapp.ui.library.manage

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.ui.edit.EditInfoActivity
import com.wanotube.wanotubeapp.ui.watch.NewWatchActivity

class ManagementAdapter : RecyclerView.Adapter<ManagementAdapter.ViewHolder>() {
    
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
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(
        itemView
    ){

        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitle)
        private val thumbnailVideoView: ImageView = itemView.findViewById(R.id.thumbnail_video)
        private val menuView: ImageView = itemView.findViewById(R.id.video_menu)
        private val copyrightView: MaterialTextView = itemView.findViewById(R.id.copyright)

        fun bind(item: Video) {
            titleView.text = item.title
            val subtitle = item.totalViews.toString() + " views"
            subtitleView.text = subtitle

            Glide.with(thumbnailVideoView.context)
                .load(item.thumbnail)
                .placeholder(R.drawable.video_placeholder)
                .centerCrop()
                .override(480, 269)
                .into(thumbnailVideoView)

            val context = thumbnailVideoView.context
            thumbnailVideoView.setOnClickListener{
                val intent = Intent(context, NewWatchActivity::class.java)
                intent.putExtra("VIDEO_ID", item.id)
                intent.putExtra("NEED_TOKEN", true)
                context.startActivity(intent)
            }
            menuView.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(context)
                bottomSheetDialog.apply {
                    setContentView(R.layout.manage_video_menu_dialog)
                    findViewById<LinearLayout>(R.id.edit_video)?.setOnClickListener {
                        val intent = Intent(context, EditInfoActivity::class.java)
                        intent.putExtra("VIDEO_ID", item.id)
                        context.startActivity(intent)
                    }
//                    findViewById<LinearLayout>(R.id.delete_video)?.setOnClickListener {
//                    }
                    show()
                }
            }
            if (item.recognitionResultTitle?.isNotEmpty() == true) {
                copyrightView.visibility = View.VISIBLE
            }
            copyrightView.setOnClickListener {
                val resources = context.resources
                val copyrightLabel = "${resources.getString(R.string.copyright_owners)}: ${item.recognitionResultLabel}"
                val copyrightTitle = "${item.recognitionResultTitle} - ${ item.recognitionResultArtist}"
                MaterialAlertDialogBuilder(context)
                    .setTitle(resources.getString(R.string.copyright_claim_normal))
                    .setMessage(copyrightTitle + "\n" + copyrightLabel)
                    .show()
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.management_video_component_list, parent, false)
                return ViewHolder(view)
            }
        }
    }
}