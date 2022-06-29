package com.wanotube.wanotubeapp.ui.library.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.WanoTubeActivity
import com.wanotube.wanotubeapp.databinding.ActivityManagementBinding
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.util.MarginItemDecoration
import com.wanotube.wanotubeapp.util.VideoType
import com.wanotube.wanotubeapp.viewmodels.ChannelViewModel


class ManagementActivity : WanoTubeActivity() {

    var currentVideos: List<Video> = listOf()
    var shortVideos: List<Video> = listOf()
    var normalvideos: List<Video> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ChannelViewModel.ChannelViewModelFactory(application)

        val channelViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(ChannelViewModel::class.java)
        channelViewModel.refreshVideos()

        binding.channelViewModel = channelViewModel

        val adapter = ManagementAdapter()

        binding.videoManagementList.apply {
            this.adapter = adapter
            binding.managementShimmerViewContainer.startShimmer()

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

        channelViewModel.currentChannelVideos.observe(this) {
            it?.let {
                normalvideos = it.filter {
                    video -> video.type == VideoType.NORMAL.name
                }
                shortVideos = it.filter {
                    video -> video.type == VideoType.SHORT.name
                }
                currentVideos = normalvideos
                adapter.data = currentVideos
                binding.managementShimmerViewContainer.stopShimmer()
                binding.managementShimmerViewContainer.visibility = View.GONE
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

        supportActionBar?.title = "My Videos"

        val tabLayout = binding.tabLayout
        tabLayout.apply {
            addTab(tabLayout.newTab().setText("Normal"))
            addTab(tabLayout.newTab().setText("Short"))
            tabGravity = TabLayout.GRAVITY_FILL
        }
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        adapter.data = normalvideos
                    }
                    1 -> {
                        adapter.data = shortVideos

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}