package com.wanotube.wanotubeapp.ui.library.watchhistory

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.databinding.ActivityWatchHistoryBinding
import com.wanotube.wanotubeapp.ui.library.watchlater.WatchLaterAdapter
import com.wanotube.wanotubeapp.util.VideoType
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel

class WatchHistoryActivity : WanoTubeActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWatchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        val viewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(WanoTubeViewModel::class.java)

        viewModel.getWatchHistoryList()

        binding.viewModel = viewModel

        val adapter = WatchHistoryAdapter(application)

        binding.watchHistoryList.adapter = adapter

        viewModel.watchHistoryList.observe(this) {
            it?.let {
                adapter.data = it
//                binding.homeShimmerViewContainer.stopShimmer()
//                binding.homeShimmerViewContainer.visibility = View.GONE
                binding.pullToRefresh.visibility = View.VISIBLE
            }
        }

        val pullToRefresh: SwipeRefreshLayout = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            viewModel.getWatchHistoryList()
            pullToRefresh.isRefreshing = false
        }

        binding.lifecycleOwner = this
    }
}