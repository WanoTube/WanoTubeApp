package com.wanotube.wanotubeapp.ui.manage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wanotube.wanotubeapp.IEventListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.databinding.FragmentManagementBinding
import com.wanotube.wanotubeapp.util.MarginItemDecoration
import com.wanotube.wanotubeapp.viewmodels.ChannelViewModel

class ManagementFragment : Fragment() {

    private lateinit var listener: IEventListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as IEventListener
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener.  */
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentManagementBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_management, container, false
        )

        val application = requireNotNull(this.activity).application

        val viewModelFactory = ChannelViewModel.ChannelViewModelFactory(application)

        val channelViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(ChannelViewModel::class.java)

        binding.channelViewModel = channelViewModel

        val adapter = ManagementAdapter(listener)

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

        channelViewModel.currentChannelVideos.observe(viewLifecycleOwner) {
            it?.let {
                adapter.data = it
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
        
        return binding.root
    }
}