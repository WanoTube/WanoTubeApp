package com.wanotube.wanotubeapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wanotube.wanotubeapp.IEventListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.databinding.FragmentHomeBinding
import com.wanotube.wanotubeapp.repository.VideosRepository
import com.wanotube.wanotubeapp.util.VideoType
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel


class HomeFragment : Fragment(), IEventListener {

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
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        val application = requireNotNull(this.activity).application

        val viewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        val viewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(WanoTubeViewModel::class.java)

        binding.viewModel = viewModel

        val adapter = HomeAdapter(application, listener)

        binding.videoList.adapter = adapter
        
        binding.homeShimmerViewContainer.startShimmer()
        viewModel.channels.observe(viewLifecycleOwner) {
            it?.let {
                adapter.channels = it
            }
        }
        
        viewModel.allPublicVideos.observe(viewLifecycleOwner) {
            it?.let {
                val videos = it.filter { 
                    video -> video.type == VideoType.NORMAL.name
                }
                adapter.data = videos
                binding.homeShimmerViewContainer.stopShimmer()
                binding.homeShimmerViewContainer.visibility = View.GONE
                binding.pullToRefresh.visibility = View.VISIBLE
            }
        }

        binding.lifecycleOwner = this

        val pullToRefresh: SwipeRefreshLayout = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            viewModel.clearDataFromRepository()
            viewModel.refreshDataFromRepository()
            pullToRefresh.isRefreshing = false
        }
        
        return binding.root
    }
}