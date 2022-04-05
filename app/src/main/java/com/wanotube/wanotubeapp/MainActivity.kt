package com.wanotube.wanotubeapp

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wanotube.wanotubeapp.databinding.ActivityMainBinding
import com.wanotube.wanotubeapp.ui.following.FollowingFragment
import com.wanotube.wanotubeapp.ui.home.HomeFragment
import com.wanotube.wanotubeapp.ui.profile.ProfileFragment
import com.wanotube.wanotubeapp.ui.shorts.ShortsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var  currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        customActionBar()

        currentFragment = HomeFragment()
        loadFragment(currentFragment)

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
        findViewById<FrameLayout>(R.id.myNavHostFragment).findNavController().navigate(R.id.fragment_home)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        if (currentFragment is IOnFocusListenable) {
//            (currentFragment as IOnFocusListenable).onWindowFocusChanged(hasFocus)
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        (currentFragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
            super.onBackPressed()
        }
    }
}


interface IOnFocusListenable {
    fun onWindowFocusChanged(hasFocus: Boolean)
}

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}