package com.wanotube.wanotubeapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_UNLABELED
import com.wanotube.wanotubeapp.databinding.ActivityMainBinding
import com.wanotube.wanotubeapp.ui.edit.UploadActivity
import com.wanotube.wanotubeapp.ui.home.HomeFragment
import com.wanotube.wanotubeapp.ui.manage.ManagementFragment
import com.wanotube.wanotubeapp.ui.profile.ProfileFragment
import com.wanotube.wanotubeapp.ui.shorts.ShortsFragment
import com.wanotube.wanotubeapp.util.Constant
import com.wanotube.wanotubeapp.util.URIPathHelper

class MainActivity : WanoTubeActivity(), IEventListener {

    private lateinit var  currentFragment: Fragment
    private lateinit var binding: ActivityMainBinding
    private var  currentFragmentId: Int = R.id.home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentFragment = HomeFragment()
        currentFragmentId =  R.id.home
        loadFragment(R.id.fragment_home)

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
            if (currentFragmentId == item.itemId && !isCreateFragment()) {
                false
            } else {
                var validFlag = true
                currentFragmentId = item.itemId
                
                when(item.itemId) {
                    R.id.home -> {
                        currentFragment = HomeFragment()
                        loadFragment(R.id.fragment_home)
                    }
                    R.id.shorts -> {
                        currentFragment = ShortsFragment()
                        loadFragment(R.id.fragment_short)
                    }
                    R.id.management -> {
                        currentFragment = ManagementFragment()
                        loadFragment(R.id.fragment_management)
                    }
                    R.id.user -> {
                        currentFragment = ProfileFragment()
                        loadFragment(R.id.fragment_profile)
                    }
                    R.id.create -> {
                        if (checkTokenAvailable()) {
                            showBottomSheetDialog()
                        } else {
                            openLoginActivity()
                        }
                    }
                    else -> validFlag = false
                }
                item.isChecked = false
                validFlag
            }
        }
    }
    
    private fun isCreateFragment(): Boolean {
        return currentFragmentId == R.id.create
    }

    private fun loadFragment(fragmentId: Int) {
        findViewById<FrameLayout>(R.id.myNavHostFragment).findNavController().navigate(fragmentId)
    }

    private fun openGalleryForVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        binding.bottomNavigation.labelVisibilityMode = LABEL_VISIBILITY_UNLABELED
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

    override fun setCurrentFragment(fragment: Fragment) {
        currentFragment = fragment
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.apply {
            setContentView(R.layout.bottom_sheet_dialog_layout)
            findViewById<LinearLayout>(R.id.upload_video)?.setOnClickListener {
                openGalleryForVideo()
                bottomSheetDialog.dismiss()
            }
            findViewById<LinearLayout>(R.id.create_short)?.setOnClickListener {
                openGalleryForVideo()
                bottomSheetDialog.dismiss()
            }
            show()
        }

    }
}

interface IEventListener {
    fun setCurrentFragment(fragment: Fragment)
}