package com.wanotube.wanotubeapp.ui.login

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class LoginAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private lateinit var context: Context 
    private var totalTabs: Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginTabFragment()
            1 -> SignUpTabFragment()
            else -> LoginTabFragment()
        }
    }

    override fun getItemCount(): Int {
        return totalTabs
    }
}