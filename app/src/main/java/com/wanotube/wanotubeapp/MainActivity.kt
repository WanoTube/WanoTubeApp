package com.wanotube.wanotubeapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_UNLABELED
import com.wanotube.wanotubeapp.databinding.ActivityMainBinding
import com.wanotube.wanotubeapp.ui.edit.UploadActivity
import com.wanotube.wanotubeapp.ui.following.FollowingFragment
import com.wanotube.wanotubeapp.ui.home.HomeFragment
import com.wanotube.wanotubeapp.ui.profile.ProfileFragment
import com.wanotube.wanotubeapp.ui.shorts.ShortsFragment
import com.wanotube.wanotubeapp.util.Constant
import com.wanotube.wanotubeapp.util.URIPathHelper

class MainActivity : WanoTubeActivity(), IEventListener {

    private lateinit var  currentFragment: Fragment
    private lateinit var binding: ActivityMainBinding

    private var  currentFragmentId: Int = R.id.home
    private var  previousFragmentId: Int = R.id.home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentFragment = HomeFragment()
        currentFragmentId =  R.id.home
        loadFragment(currentFragment)

        val navigationBarView = binding.bottomNavigation
        customNavigation(navigationBarView)
    }

    override fun customActionBar() {
        super.customActionBar()
        supportActionBar!!.apply {
            setDisplayShowCustomEnabled(true)
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_home_action_bar_layout)
        }
    }

    private fun customNavigation(navigationBarView: BottomNavigationView) {

        navigationBarView.setOnItemSelectedListener { item ->
            if (currentFragmentId == item.itemId) {
                false
            } else {
                var validFlag = true

                currentFragmentId = item.itemId
                when(item.itemId) {
                    R.id.home -> {
                        currentFragment = HomeFragment()
                        loadFragment(currentFragment)
                    }
                    R.id.shorts -> {
                        currentFragment = ShortsFragment()
                        loadFragment(currentFragment)
                    }
                    R.id.following -> {
                        currentFragment = FollowingFragment()
                        loadFragment(currentFragment)
                    }
                    R.id.user -> {
                        currentFragment = ProfileFragment()
                        loadFragment(currentFragment)
                    }
                    R.id.create -> {
                        openGalleryForVideo()
                    }
                    else -> validFlag = false
                }
                item.isChecked = false

                previousFragmentId = item.itemId

                validFlag
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
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        binding.bottomNavigation.apply {
            selectedItemId = previousFragmentId
            labelVisibilityMode = LABEL_VISIBILITY_UNLABELED
        }

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