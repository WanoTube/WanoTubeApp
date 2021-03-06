package com.wanotube.wanotubeapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_UNLABELED
import com.wanotube.wanotubeapp.databinding.ActivityMainBinding
import com.wanotube.wanotubeapp.ui.deepar.CameraActivity
import com.wanotube.wanotubeapp.ui.home.HomeFragment
import com.wanotube.wanotubeapp.ui.library.LibraryFragment
import com.wanotube.wanotubeapp.ui.profile.ProfileFragment
import com.wanotube.wanotubeapp.ui.shorts.ShortsFragment
import com.wanotube.wanotubeapp.util.Constant
import com.wanotube.wanotubeapp.util.URIPathHelper
import com.wanotube.wanotubeapp.viewmodels.WanoTubeViewModel

class MainActivity : WanoTubeActivity(), IEventListener {

    private lateinit var  currentFragment: Fragment
    private lateinit var binding: ActivityMainBinding
    private var  currentFragmentId: Int = R.id.home
    private var isUploadNormalVideo = true
    
    private lateinit var videoViewModel: WanoTubeViewModel
    private var isCheckCopyrightStatus = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentFragment = HomeFragment()
        currentFragmentId =  R.id.home
        loadFragment(R.id.fragment_home)

        val navigationBarView = binding.bottomNavigation
        customNavigation(navigationBarView)

        initData()
    }

    private fun initData() {
        val videoViewModelFactory = WanoTubeViewModel.WanoTubeViewModelFactory(application)

        videoViewModel =
            ViewModelProvider(
                this, videoViewModelFactory
            ).get(WanoTubeViewModel::class.java)

        initVideos()
    }
    
    private fun initVideos() {
        videoViewModel.refreshDataFromRepository()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoViewModel.clearDataFromRepository()
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
                        if (checkTokenAvailable(true)) {
                            currentFragment = LibraryFragment()
                            loadFragment(R.id.fragment_library)
                        }
                    }
                    R.id.user -> {
                        if (checkTokenAvailable(true)) {
                            currentFragment = ProfileFragment()
                            loadFragment(R.id.fragment_profile)
                        }
                    }
                    R.id.create -> {
                        if (checkTokenAvailable(true)) {
                            if (!isCheckCopyrightStatus) {
                                getCopyrightStatus()
                                isCheckCopyrightStatus = true
                            }

                            val isBlockedStatus = isBlockedStatus()
                            if (!isBlockedStatus) {
                                showBottomSheetDialog()
                            } else {
                                MaterialAlertDialogBuilder(this)
                                    .setTitle(resources.getString(R.string.blocked_status_title))
                                    .setMessage(resources.getString(R.string.blocked_status_caption))
                                    .show()
                            }
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
                        uploadVideo(videoFullPath, isUploadNormalVideo)
                    }
                }
            }
        }
    }

//    override fun setCurrentFragment(fragment: Fragment) {
//        currentFragment = fragment
//    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.apply {
            setContentView(R.layout.choose_video_dialog_layout)
            findViewById<LinearLayout>(R.id.upload_video)?.setOnClickListener {
                chooseVideoForUploadNormalVideo(bottomSheetDialog, true)
            }
            findViewById<LinearLayout>(R.id.create_short)?.setOnClickListener {
                openCameraActivity()
//                chooseVideoForUploadNormalVideo(bottomSheetDialog, false)
            }
            show()
        }

    }
    
    private fun openCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
    
    private fun chooseVideoForUploadNormalVideo(bottomSheetDialog: BottomSheetDialog, isUploadNormalVideo: Boolean) {
        openGalleryForVideo()
        bottomSheetDialog.dismiss()
        this.isUploadNormalVideo = isUploadNormalVideo
        application
    }
}

interface IEventListener {
//    fun setCurrentFragment(fragment: Fragment)
}