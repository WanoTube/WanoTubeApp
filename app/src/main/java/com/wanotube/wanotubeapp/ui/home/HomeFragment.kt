package com.wanotube.wanotubeapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.databinding.FragmentHomeBinding
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false)

        val application = requireNotNull(this.activity).application

        val viewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        val videoViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(WanoTubeViewModel::class.java)

        binding.videoViewModel = videoViewModel

        val adapter = HomeAdapter()

        binding.videoList.adapter = adapter

        videoViewModel.playlist.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        val dataAdapter = adapter.data
        Log.e("Ngan", "dataAdapter: $dataAdapter")

        binding.lifecycleOwner = this

        return binding.root
    }
}