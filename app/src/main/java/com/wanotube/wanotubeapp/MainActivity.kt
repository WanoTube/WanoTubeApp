package com.wanotube.wanotubeapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wanotube.wanotubeapp.databinding.ActivityMainBinding
import com.wanotube.wanotubeapp.ui.following.FollowingFragment
import com.wanotube.wanotubeapp.ui.home.HomeFragment
import com.wanotube.wanotubeapp.ui.profile.ProfileFragment
import com.wanotube.wanotubeapp.ui.shorts.ShortsFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        customActionBar()
        loadFragment(HomeFragment())

        val navigationBarView = binding.bottomNavigation
        customNavigation(navigationBarView)
    }

    private fun customActionBar() {

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.custom_action_bar_layout)
        val view: View = supportActionBar!!.customView

        val backButton = view.findViewById(R.id.action_bar_back) as ImageButton
        backButton.setOnClickListener { finish() }

        val forwardButton = view.findViewById(R.id.action_bar_forward) as ImageButton
        forwardButton.setOnClickListener {
            Toast.makeText(applicationContext, "Forward Button is clicked", Toast.LENGTH_LONG)
                .show()
        }


    }
    private fun customNavigation(navigationBarView: BottomNavigationView) {

        navigationBarView.setOnItemSelectedListener { item ->
            val fragment: Fragment

            when(item.itemId) {
                R.id.home -> {
                    fragment = HomeFragment()
                    loadFragment(fragment)

                    true
                }
                R.id.shorts -> {
                    fragment = ShortsFragment()
                    loadFragment(fragment)

                    true
                }
                R.id.following -> {
                    fragment = FollowingFragment()
                    loadFragment(fragment)

                    true
                }
                R.id.user -> {
                    fragment = ProfileFragment()
                    loadFragment(fragment)

                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}