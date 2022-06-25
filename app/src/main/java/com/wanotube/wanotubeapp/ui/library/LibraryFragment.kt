package com.wanotube.wanotubeapp.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.network.objects.NetworkCopyrightStatus
import com.wanotube.wanotubeapp.network.objects.NetworkFollow
import com.wanotube.wanotubeapp.repository.ChannelRepository
import com.wanotube.wanotubeapp.ui.library.following.FollowingActivity
import com.wanotube.wanotubeapp.ui.library.manage.ManagementActivity
import com.wanotube.wanotubeapp.ui.library.watchhistory.WatchHistoryActivity
import com.wanotube.wanotubeapp.ui.library.watchlater.WatchLaterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class LibraryFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        view.findViewById<LinearLayout>(R.id.watch_history).setOnClickListener {
            openWatchHistoryActivity()
        }
        
        view.findViewById<LinearLayout>(R.id.my_videos).setOnClickListener {
            openMyVideoActivity()
        }
        
        view.findViewById<LinearLayout>(R.id.my_followings).setOnClickListener {
            openMyFollowingsActivity()
        }
        
        view.findViewById<LinearLayout>(R.id.watch_later).setOnClickListener {
            openWatchLaterActivity()
        }
        val blockedStatusTextView = view.findViewById<TextView>(R.id.blocked_status)
        val strikeTextView = view.findViewById<TextView>(R.id.strikes)

        val channelRepository = ChannelRepository(getDatabase(requireActivity().application))
        channelRepository.getCopyrightStatus()?.enqueue(object : Callback<NetworkCopyrightStatus> {
            override fun onResponse(
                call: Call<NetworkCopyrightStatus>?,
                response: Response<NetworkCopyrightStatus?>?
            ) {
                if (response?.code() == 200) {
                    val result = response.body()
                    val isBlockedStatus = result?.blockedStatus != "NONE"
                    view.findViewById<LinearLayout>(R.id.account_status).visibility =
                        if (isBlockedStatus) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    val status = if (isBlockedStatus) "Blocked" else "Normal"
                    blockedStatusTextView.text = "Status: " + status
                    val strikes = result?.strikes
                    strikeTextView.text = "Strikes: " + strikes?.size.toString()
                }
            }
            override fun onFailure(call: Call<NetworkCopyrightStatus>?, t: Throwable?) {
                Timber.e("Failed: error: %s", t.toString())
            }
        })
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

    private fun openWatchLaterActivity() {
        val intent = Intent(requireContext(), WatchLaterActivity::class.java)
        startActivity(intent)
    }
    
    private fun openWatchHistoryActivity() {

        val intent = Intent(requireContext(), WatchHistoryActivity::class.java)
        startActivity(intent)
    }
}