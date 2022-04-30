package com.wanotube.wanotubeapp.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wanotube.wanotubeapp.R

class LoginActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        
        tabLayout.apply {
            addTab(tabLayout.newTab().setText("Login"))
            addTab(tabLayout.newTab().setText("Sign Up"))
            tabGravity = TabLayout.GRAVITY_FILL
        }
        viewPager.adapter = LoginAdapter(this)
        
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            viewPager.setCurrentItem(tab.position, true)
            if (position == 0) tab.text = "Login"
            if (position == 1) tab.text = "Sign Up"            
        }.attach()

        supportActionBar?.hide()
    }
}