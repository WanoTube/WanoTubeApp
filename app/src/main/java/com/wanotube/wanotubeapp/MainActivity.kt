package com.wanotube.wanotubeapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wanotube.wanotubeapp.databinding.ActivityMainBinding
import com.wanotube.wanotubeapp.ui.edit.UploadActivity
import com.wanotube.wanotubeapp.ui.following.FollowingFragment
import com.wanotube.wanotubeapp.ui.home.HomeFragment
import com.wanotube.wanotubeapp.ui.profile.ProfileFragment
import com.wanotube.wanotubeapp.ui.shorts.ShortsFragment
import com.wanotube.wanotubeapp.util.Constant
import com.wanotube.wanotubeapp.util.URIPathHelper

class MainActivity : AppCompatActivity(), IEventListener {

    private lateinit var  currentFragment: Fragment
    private var  currentFragmentId: Int = R.id.home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        customActionBar()

        currentFragment = HomeFragment()
        currentFragmentId =  R.id.home
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
            if (currentFragmentId == item.itemId) {
                false
            } else {
                currentFragmentId = item.itemId
                when(item.itemId) {
                    R.id.home -> {
                        currentFragment = HomeFragment()
                        loadFragment(currentFragment)

                        true
                    }
                    R.id.shorts -> {
                        currentFragment = ShortsFragment()
                        loadFragment(currentFragment)
                        true
                    }
                    R.id.following -> {
                        currentFragment = FollowingFragment()
                        loadFragment(currentFragment)

                        true
                    }
                    R.id.user -> {
                        currentFragment = ProfileFragment()
                        loadFragment(currentFragment)

                        true
                    }
                    R.id.create -> {
                        openGalleryForVideo()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        findViewById<FrameLayout>(R.id.myNavHostFragment).findNavController().navigate(R.id.fragment_home)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (currentFragment is IOnFocusListenable) {
            (currentFragment as IOnFocusListenable).onWindowFocusChanged(hasFocus)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        (currentFragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
            super.onBackPressed()
        }
    }

    private fun openGalleryForVideo() {
        Log.e("Ngan", "openGalleryForVideo")
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(intent, "Select Video"),
            Constant.REQUEST_VIDEO
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_VIDEO){
            if (resultCode == Activity.RESULT_OK){
                if (data?.data != null) {
                    val uriPathHelper = URIPathHelper()
                    val videoFullPath = uriPathHelper.getPath(this, data.data!!)
                    if (videoFullPath != null) {
                        loadUploadActivity(videoFullPath)
//                        UploadVideoAPICall(file)
                    }
                }
            }
        }
    }

    private fun loadUploadActivity(filePath: String) {
        val intent = Intent(baseContext, UploadActivity::class.java)
        intent.putExtra("FILE_PATH", filePath)
        startActivity(intent)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val ret = super.dispatchTouchEvent(ev)
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_UP) {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        val touchCoordinates = IntArray(2)
                        view.getLocationOnScreen(touchCoordinates)
                        val x: Float = event.rawX + view.getLeft() - touchCoordinates[0]
                        val y: Float = event.rawY + view.getTop() - touchCoordinates[1]
                        //If the touch position is outside the EditText then we hide the keyboard
                        if (x < view.getLeft() || x >= view.getRight() || y < view.getTop() || y > view.getBottom()) {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                            view.clearFocus()
                        }
                    }
                }
            }
        }

        return ret
    }

    override fun setCurrentFragment(fragment: Fragment) {
        currentFragment = fragment
    }
}


interface IOnFocusListenable {
    fun onWindowFocusChanged(hasFocus: Boolean)
}

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}

interface IEventListener {
    fun setCurrentFragment(fragment: Fragment)
}