package com.wanotube.wanotubeapp.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.ui.library.following.FollowingActivity
import com.wanotube.wanotubeapp.ui.library.manage.ManagementActivity

class LibraryFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        view.findViewById<LinearLayout>(R.id.my_videos).setOnClickListener {
            openMyVideoActivity()
        }

        view.findViewById<LinearLayout>(R.id.my_followings).setOnClickListener {
            openMyFollowingsActivity()
        }
        return view
    }
    
    private fun openMyVideoActivity() {
        val intent = Intent(requireContext(), ManagementActivity::class.java)
        startActivity(intent)
    }
    
    private fun openMyFollowingsActivity() {
        val intent = Intent(requireContext(), FollowingActivity::class.java)
        startActivity(intent)
    }
}