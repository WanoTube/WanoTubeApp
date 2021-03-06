package com.wanotube.wanotubeapp.ui.library.following

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.databinding.ActivityFollowingBinding
import com.wanotube.wanotubeapp.util.MarginItemDecoration
import com.wanotube.wanotubeapp.viewmodels.ChannelViewModel

class FollowingActivity: WanoTubeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFollowingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ChannelViewModel.ChannelViewModelFactory(application)

        val channelViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(ChannelViewModel::class.java)
        channelViewModel.refreshVideos()

        binding.channelViewModel = channelViewModel

        val adapter = FollowingAdapter()

        binding.followedChannelList.apply {
            this.adapter = adapter
//            binding.managementShimmerViewContainer.startShimmer()

            val topBottomMargin = resources.getDimensionPixelSize(R.dimen.component_large_margin)
            val leftRightMargin = resources.getDimensionPixelSize(R.dimen.component_margin)

            addItemDecoration(
                MarginItemDecoration(
                    bottomSpaceSize = topBottomMargin,
                    topSpaceSize = topBottomMargin,
                    leftSpaceSize = leftRightMargin,
                    rightSpaceSize = leftRightMargin
                )
            )
        }

        channelViewModel.currentChannelFollowings.observe(this) {
            it?.let {
                adapter.data = it
//                binding.managementShimmerViewContainer.stopShimmer()
//                binding.managementShimmerViewContainer.visibility = View.GONE
                binding.pullToRefreshMyVideos.visibility = View.VISIBLE
            }
        }

        binding.lifecycleOwner = this

        val pullToRefresh: SwipeRefreshLayout = binding.pullToRefreshMyVideos
        pullToRefresh.setOnRefreshListener {
            channelViewModel.clearDataFromRepository()
            channelViewModel.refreshVideos()
            pullToRefresh.isRefreshing = false
        }

        supportActionBar?.title = "My Followings"

    }
}