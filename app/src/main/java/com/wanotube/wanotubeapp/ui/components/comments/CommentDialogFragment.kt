package com.wanotube.wanotubeapp.ui.components.comments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.databinding.DialogCommentShortBinding
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.repository.CommentRepository
import com.wanotube.wanotubeapp.ui.watch.CommentAdapter
import com.wanotube.wanotubeapp.viewmodels.CommentViewModel
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel

class CommentDialogFragment: BottomSheetDialogFragment() {
    
    var videoId = ""
    var recyclerView: RecyclerView? = null
    private var adapter: CommentAdapter? = null
    private lateinit var binding: DialogCommentShortBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_comment_short, container, false
        )

        videoId = arguments?.getString("VIDEO_ID").toString()
        activity?.application?.let {
            val commentRepository = CommentRepository(getDatabase(it))
            binding.btnSendComment.setOnClickListener {
                val commentText = binding.commentEditText.text.toString()
                binding.commentEditText.text.clear()
                adapter?.let { it1 -> commentRepository.sendComment(commentText, videoId, it1) }
                
                context?.let { it1 -> hideKeyboardFrom(
                    it1,
                    binding.commentEditText
                )}

            }
        }
        loadAvatar(binding.avatarCommentAuthor)
        initRecyclerView()
        return binding.root
    }

    private fun loadAvatar(imageView: ImageView) {
        val mAuthPreferences = AuthPreferences(imageView.context)
        val avatarUrl = mAuthPreferences.avatar

        Glide.with(imageView.context)
            .load(avatarUrl)
            .placeholder(R.drawable.image_placeholder)
            .circleCrop()
            .into(imageView)
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun initRecyclerView() {
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView = binding.allComments
        activity?.application?.let { it ->
            val viewModelFactory = CommentViewModel.CommentViewModelFactory(it)

            val commentViewModel =
                ViewModelProvider(
                    this, viewModelFactory
                ).get(CommentViewModel::class.java)

            binding.commentViewModel = commentViewModel

            adapter = CommentAdapter()

            binding.allComments.adapter = adapter

            binding.lifecycleOwner = this

            commentViewModel.comments.observe(this) {
                it?.let {
                    val videos = it.filter {
                            video ->  video.videoId == videoId
                    }
                    adapter?.comments = videos
                    binding.commentTotal.text = adapter!!.itemCount.toString()
                }
            }


            val videoViewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(it)

            val videoViewModel =
                ViewModelProvider(
                    this, videoViewModelFactory
                ).get(WanoTubeViewModel::class.java)

            videoViewModel.channels.observe(viewLifecycleOwner) {
                it?.let {
                    adapter?.channels = it
                }
            }
        }

        recyclerView?.apply {
            isNestedScrollingEnabled = true
            layoutManager = mLayoutManager
            setItemViewCacheSize(50)
            adapter = adapter
        }
    }

    companion object {
        fun createInstance(videoId: String?): CommentDialogFragment {
            val fragment = CommentDialogFragment()
            val args = Bundle()
            args.putString("VIDEO_ID", videoId)
            fragment.arguments = args //set
            return fragment
        }
    }
}
