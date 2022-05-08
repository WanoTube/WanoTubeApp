package com.wanotube.wanotubeapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wanotube.wanotubeapp.IEventListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.databinding.FragmentHomeBinding
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel


class HomeFragment : Fragment() {

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

        val adapter = HomeAdapter(listener)

        binding.videoList.adapter = adapter
        
        binding.homeShimmerViewContainer.startShimmer()

        viewModel.channels.observe(viewLifecycleOwner) {
            it?.let {
                adapter.channels = it
            }
        }
        
        viewModel.allPublicVideos.observe(viewLifecycleOwner) {
            it?.let {
                adapter.data = it
                binding.homeShimmerViewContainer.stopShimmer()
                binding.homeShimmerViewContainer.visibility = View.GONE
                binding.videoList.visibility = View.VISIBLE
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }
}