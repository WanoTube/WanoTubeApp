package com.wanotube.wanotubeapp.ui.library.watchlater

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wanotube.wanotubeapp.databinding.ActivityWatchLaterBinding
import com.wanotube.wanotubeapp.util.VideoType
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel

class WatchLaterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWatchLaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        val viewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(WanoTubeViewModel::class.java)

        binding.viewModel = viewModel
        
        val adapter = WatchLaterAdapter(application)

        binding.watchLaterList.adapter = adapter

        viewModel.watchLaterList.observe(this) {
            it?.let {
                val videos = it.filter {
                        video -> video.type == VideoType.NORMAL.name
                }
                adapter.data = videos
//                binding.homeShimmerViewContainer.stopShimmer()
//                binding.homeShimmerViewContainer.visibility = View.GONE
                binding.pullToRefresh.visibility = View.VISIBLE
            }
        }
        
        val pullToRefresh: SwipeRefreshLayout = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            viewModel.getWatchLaterList()
            pullToRefresh.isRefreshing = false
        }

        binding.lifecycleOwner = this
    }
}