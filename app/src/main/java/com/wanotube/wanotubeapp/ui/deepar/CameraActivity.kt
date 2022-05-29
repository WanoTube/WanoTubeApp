package com.wanotube.wanotubeapp.ui.deepar

import androidx.appcompat.app.AppCompatActivity
import android.view.SurfaceHolder
import ai.deepar.ar.AREventListener
import androidx.camera.core.CameraSelector
import com.wanotube.wanotubeapp.deepar.ARSurfaceProvider
import androidx.camera.lifecycle.ProcessCameraProvider
import ai.deepar.ar.DeepAR
import android.os.Bundle
import com.wanotube.wanotubeapp.R
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.widget.ImageButton
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import android.util.DisplayMetrics
import android.content.pm.ActivityInfo
import ai.deepar.ar.CameraResolutionPreset
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.camera.core.ImageAnalysis
import ai.deepar.ar.DeepARImageFormat
import android.graphics.Bitmap
import ai.deepar.ar.ARErrorType
import android.Manifest
import android.media.Image
import android.os.Environment
import android.text.format.DateFormat
import android.util.Size
import android.view.Surface
import android.view.View
import android.widget.RadioButton
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.concurrent.ExecutionException

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback, AREventListener {
    // Default camera lens value, change to CameraSelector.LENS_FACING_BACK to initialize with back camera
    private val defaultLensFacing = CameraSelector.LENS_FACING_FRONT
    private var surfaceProvider: ARSurfaceProvider? = null
    private var lensFacing = defaultLensFacing
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private lateinit var buffers: Array<ByteBuffer?>
    private var currentBuffer = 0
    private var deepAR: DeepAR? = null
    private var currentMask = 0
    private var currentEffect = 0
    private var currentFilter = 0// if the device's natural orientation is portrait:

    /*
           get interface orientation from
           https://stackoverflow.com/questions/10380989/how-do-i-get-the-current-orientation-activityinfo-screen-orientation-of-an-a/10383164
        */
    private val screenOrientation: Int
        get() {
            val rotation = windowManager.defaultDisplay.rotation
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            width = dm.widthPixels
            height = dm.heightPixels
            // if the device's natural orientation is portrait:
            val orientation: Int = if ((rotation == Surface.ROTATION_0
                        || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height
            ) {
                when (rotation) {
                    Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            } else {
                when (rotation) {
                    Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
            return orientation
        }
    var masks: ArrayList<String>? = null
    var effects: ArrayList<String>? = null
    var filters: ArrayList<String>? = null
    private var activeFilterType = 0
    private var recording = false
    private var currentSwitchRecording = false
    private var width = 0
    private var height = 0
    private var videoFileName: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity)
    }

    override fun onStart() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO),
                1)
        } else {
            // Permission has already been granted
            initialize()
        }
        super.onStart()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.size > 0) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return  // no permission
                }
            }
            initialize()
        }
    }

    private fun initialize() {
        initializeDeepAR()
        initializeFilters()
        initalizeViews()
    }

    private fun initializeFilters() {
        masks = ArrayList()
        masks!!.add("none")
        masks!!.add("aviators")
        masks!!.add("bigmouth")
        masks!!.add("dalmatian")
        masks!!.add("flowers")
        masks!!.add("koala")
        masks!!.add("lion")
        masks!!.add("smallface")
        masks!!.add("teddycigar")
        masks!!.add("background_segmentation")
        masks!!.add("tripleface")
        masks!!.add("sleepingmask")
        masks!!.add("fatify")
        masks!!.add("obama")
        masks!!.add("mudmask")
        masks!!.add("pug")
        masks!!.add("slash")
        masks!!.add("twistedface")
        masks!!.add("grumpycat")
        masks!!.add("Helmet_PBR_V1")
        effects = ArrayList()
        effects!!.add("none")
        effects!!.add("fire")
        effects!!.add("rain")
        effects!!.add("heart")
        effects!!.add("blizzard")
        filters = ArrayList()
        filters!!.add("none")
        filters!!.add("filmcolorperfection")
        filters!!.add("tv80")
        filters!!.add("drawingmanga")
        filters!!.add("sepia")
        filters!!.add("bleachbypass")
    }

    private fun initalizeViews() {
        val previousMask = findViewById<ImageButton>(R.id.previousMask)
        val nextMask = findViewById<ImageButton>(R.id.nextMask)
        val radioMasks = findViewById<RadioButton>(R.id.masks)
        val radioEffects = findViewById<RadioButton>(R.id.effects)
        val radioFilters = findViewById<RadioButton>(R.id.filters)
        val arView = findViewById<SurfaceView>(R.id.surface)
        arView.holder.addCallback(this)

        // Surface might already be initialized, so we force the call to onSurfaceChanged
        arView.visibility = View.GONE
        arView.visibility = View.VISIBLE
        val screenshotBtn = findViewById<ImageButton>(R.id.recordButton)
        screenshotBtn.setOnClickListener { deepAR!!.takeScreenshot() }
        val switchCamera = findViewById<ImageButton>(R.id.switchCamera)
        switchCamera.setOnClickListener {
            lensFacing =
                if (lensFacing == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
            //unbind immediately to avoid mirrored frame.
            var cameraProvider: ProcessCameraProvider? = null
            try {
                cameraProvider = cameraProviderFuture!!.get()
                cameraProvider.unbindAll()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            setupCamera()
        }

//        ImageButton openActivity = findViewById(R.id.openActivity);
//        openActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(MainActivity.this, BasicActivity.class);
//                MainActivity.this.startActivity(myIntent);
//            }
//
//
//        });
        val screenShotModeButton = findViewById<TextView>(R.id.screenshotModeButton)
        val recordModeBtn = findViewById<TextView>(R.id.recordModeButton)
        recordModeBtn.background.alpha = 0x00
        screenShotModeButton.background.alpha = 0xA0
        screenShotModeButton.setOnClickListener(View.OnClickListener {
            if (currentSwitchRecording) {
                if (recording) {
                    Toast.makeText(applicationContext,
                        "Cannot switch to screenshots while recording!",
                        Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                recordModeBtn.background.alpha = 0x00
                screenShotModeButton.background.alpha = 0xA0
                screenshotBtn.setOnClickListener { deepAR!!.takeScreenshot() }
                currentSwitchRecording = !currentSwitchRecording
            }
        })
        recordModeBtn.setOnClickListener {
            if (!currentSwitchRecording) {
                recordModeBtn.background.alpha = 0xA0
                screenShotModeButton.background.alpha = 0x00
                screenshotBtn.setOnClickListener {
                    if (recording) {
                        deepAR!!.stopVideoRecording()
                        Toast.makeText(applicationContext,
                            "Recording " + videoFileName!!.name + " saved.",
                            Toast.LENGTH_LONG).show()
                    } else {
                        videoFileName = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                            "video_" + SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(
                                Date()) + ".mp4")
                        deepAR!!.startVideoRecording(videoFileName.toString(),
                            width / 2,
                            height / 2)
                        Toast.makeText(applicationContext, "Recording started.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    recording = !recording
                }
                currentSwitchRecording = !currentSwitchRecording
            }
        }
        previousMask.setOnClickListener { gotoPrevious() }
        nextMask.setOnClickListener { gotoNext() }
        radioMasks.setOnClickListener {
            radioEffects.isChecked = false
            radioFilters.isChecked = false
            activeFilterType = 0
        }
        radioEffects.setOnClickListener {
            radioMasks.isChecked = false
            radioFilters.isChecked = false
            activeFilterType = 1
        }
        radioFilters.setOnClickListener {
            radioEffects.isChecked = false
            radioMasks.isChecked = false
            activeFilterType = 2
        }
    }

    private fun initializeDeepAR() {
        deepAR = DeepAR(this)
        deepAR!!.setLicenseKey("5028032f91b374f1ba4de71571503d86e872c65da07f915446d7290ef5f5707b3268b74d0b8d9aea")
        deepAR!!.initialize(this, this)
        setupCamera()
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture!!.addListener({
            try {
                val cameraProvider = cameraProviderFuture!!.get()
                bindImageAnalysis(cameraProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindImageAnalysis(cameraProvider: ProcessCameraProvider) {
        val cameraResolutionPreset = CameraResolutionPreset.P1920x1080
        val width: Int
        val height: Int
        val orientation = screenOrientation
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            width = cameraResolutionPreset.width
            height = cameraResolutionPreset.height
        } else {
            width = cameraResolutionPreset.height
            height = cameraResolutionPreset.width
        }
        val cameraResolution = Size(width, height)
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        if (useExternalCameraTexture) {
            val preview = Preview.Builder()
                .setTargetResolution(cameraResolution)
                .build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector, preview)
            if (surfaceProvider == null) {
                surfaceProvider = ARSurfaceProvider(this, deepAR!!)
            }
            preview.setSurfaceProvider(surfaceProvider)
            surfaceProvider!!.setMirror(lensFacing == CameraSelector.LENS_FACING_FRONT)
        } else {
            buffers = arrayOfNulls(NUMBER_OF_BUFFERS)
            for (i in 0 until NUMBER_OF_BUFFERS) {
                buffers[i] = ByteBuffer.allocateDirect(width * height * 3)
                buffers[i]?.order(ByteOrder.nativeOrder())
                buffers[i]?.position(0)
            }
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(cameraResolution)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageAnalyzer)
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector, imageAnalysis)
        }
    }

    private val imageAnalyzer = ImageAnalysis.Analyzer { image ->
        val byteData: ByteArray
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        byteData = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer[byteData, 0, ySize]
        vBuffer[byteData, ySize, vSize]
        uBuffer[byteData, ySize + vSize, uSize]
        buffers[currentBuffer]!!.put(byteData)
        buffers[currentBuffer]!!.position(0)
        if (deepAR != null) {
            deepAR!!.receiveFrame(buffers[currentBuffer],
                image.width, image.height,
                image.imageInfo.rotationDegrees,
                lensFacing == CameraSelector.LENS_FACING_FRONT,
                DeepARImageFormat.YUV_420_888,
                image.planes[1].pixelStride
            )
        }
        currentBuffer = (currentBuffer + 1) % NUMBER_OF_BUFFERS
        image.close()
    }

    private fun getFilterPath(filterName: String): String? {
        return if (filterName == "none") {
            null
        } else "file:///android_asset/$filterName"
    }

    private fun gotoNext() {
        if (activeFilterType == 0) {
            currentMask = (currentMask + 1) % masks!!.size
            deepAR!!.switchEffect("mask", getFilterPath(masks!![currentMask]))
        } else if (activeFilterType == 1) {
            currentEffect = (currentEffect + 1) % effects!!.size
            deepAR!!.switchEffect("effect", getFilterPath(effects!![currentEffect]))
        } else if (activeFilterType == 2) {
            currentFilter = (currentFilter + 1) % filters!!.size
            deepAR!!.switchEffect("filter", getFilterPath(filters!![currentFilter]))
        }
    }

    private fun gotoPrevious() {
        if (activeFilterType == 0) {
            currentMask = (currentMask - 1 + masks!!.size) % masks!!.size
            deepAR!!.switchEffect("mask", getFilterPath(masks!![currentMask]))
        } else if (activeFilterType == 1) {
            currentEffect = (currentEffect - 1 + effects!!.size) % effects!!.size
            deepAR!!.switchEffect("effect", getFilterPath(effects!![currentEffect]))
        } else if (activeFilterType == 2) {
            currentFilter = (currentFilter - 1 + filters!!.size) % filters!!.size
            deepAR!!.switchEffect("filter", getFilterPath(filters!![currentFilter]))
        }
    }

    override fun onStop() {
        recording = false
        currentSwitchRecording = false
        var cameraProvider: ProcessCameraProvider? = null
        try {
            cameraProvider = cameraProviderFuture!!.get()
            cameraProvider.unbindAll()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (surfaceProvider != null) {
            surfaceProvider!!.stop()
            surfaceProvider = null
        }
        deepAR!!.release()
        deepAR = null
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (surfaceProvider != null) {
            surfaceProvider!!.stop()
        }
        if (deepAR == null) {
            return
        }
        deepAR!!.setAREventListener(null)
        deepAR!!.release()
        deepAR = null
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // If we are using on screen rendering we have to set surface view where DeepAR will render
        deepAR!!.setRenderSurface(holder.surface, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (deepAR != null) {
            deepAR!!.setRenderSurface(null, 0, 0)
        }
    }

    override fun screenshotTaken(bitmap: Bitmap) {
        val now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date())
        try {
            val imageFile =
                File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_$now.jpg")
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            //            MediaScannerConnection.scanFile(MainActivity.this, new String[]{imageFile.toString()}, null, null);
//            Toast.makeText(MainActivity.this, "Screenshot " + imageFile.getName() + " saved.", Toast.LENGTH_SHORT).show();
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun videoRecordingStarted() {}
    override fun videoRecordingFinished() {}
    override fun videoRecordingFailed() {}
    override fun videoRecordingPrepared() {}
    override fun shutdownFinished() {}
    override fun initialized() {
        // Restore effect state after deepar release
        deepAR!!.switchEffect("mask", getFilterPath(masks!![currentMask]))
        deepAR!!.switchEffect("effect", getFilterPath(effects!![currentEffect]))
        deepAR!!.switchEffect("filter", getFilterPath(filters!![currentFilter]))
    }

    override fun faceVisibilityChanged(b: Boolean) {}
    override fun imageVisibilityChanged(s: String, b: Boolean) {}
    override fun frameAvailable(image: Image) {}
    override fun error(arErrorType: ARErrorType, s: String) {}
    override fun effectSwitched(s: String) {}

    companion object {
        private const val NUMBER_OF_BUFFERS = 2
        private const val useExternalCameraTexture = true
    }
}