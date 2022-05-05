package com.wanotube.wanotubeapp.ui.manage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wanotube.wanotubeapp.IEventListener
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.databinding.FragmentManagementBinding
import com.wanotube.wanotubeapp.util.MarginItemDecoration
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel

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

        val viewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        val videoViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(WanoTubeViewModel::class.java)

        binding.videoViewModel = videoViewModel

        val adapter = ManagementAdapter(listener)

        binding.videoManagementList.apply {
            this.adapter = adapter
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

        videoViewModel.playlist.observe(viewLifecycleOwner) {
            it?.let {
                adapter.data = it
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }
}