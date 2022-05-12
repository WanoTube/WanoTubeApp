package com.wanotube.wanotubeapp.ui.shorts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.databinding.FragmentShortsBinding
import com.wanotube.wanotubeapp.util.VideoType
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel

class ShortsFragment : Fragment() {
    private lateinit var binding: FragmentShortsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_shorts, container, false
        )
        val application = requireNotNull(this.activity).application

        val viewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        val videoViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(WanoTubeViewModel::class.java)

        binding.videoViewModel = videoViewModel

        val adapter = ShortAdapter()

        binding.shortVideoList.adapter = adapter

        videoViewModel.allPublicVideos.observe(viewLifecycleOwner) {
            it?.let {
//                val videos = it.filter {
//                    video -> video.type == VideoType.SHORT.name
//                }
                adapter.data = it
            }
        }

        binding.lifecycleOwner = this
        
        return binding.root    
    }
}