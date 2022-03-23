package com.wanotube.wanotubeapp

import android.os.Bundle
import android.view.Menu
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

        supportActionBar!!.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setCustomView(R.layout.custom_action_bar_layout)
            elevation = 0f
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        val inflater = menuInflater
//        inflater.inflate(R.menu.toolbar_icon_menu, menu)
//        return true
//    }

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