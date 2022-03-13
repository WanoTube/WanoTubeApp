package com.wanotube.wanotubeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.wanotube.wanotubeapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        loadFragment(HomeFragment())

        val navigationBarView = binding.bottomNavigation
        navigationBarView.setOnItemSelectedListener { item ->
            val fragment: Fragment

            when(item.itemId) {
                R.id.page_1 -> {
                    // Respond to navigation item 1 click
                    fragment = HomeFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.page_2 -> {
                    // Respond to navigation item 2 click
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