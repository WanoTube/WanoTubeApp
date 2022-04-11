package com.wanotube.wanotubeapp.ui.edit

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.util.stringForTime
import java.io.File

class EditActivity : AppCompatActivity() {
    var inputPath = ""
    var startPosition = ""
    var endPosition =  ""
    var outputPath = ""
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        initFfmpeg()
    }

    private fun initFfmpeg() {
        val ff = FFmpeg.getInstance(this)
        ff.loadBinary(object : FFmpegLoadBinaryResponseHandler {
            override fun onFinish() {
                Log.e("FFmpegLoad", "onFinish")
            }

            override fun onSuccess() {

                Log.e("FFmpegLoad", "onSuccess")

                inputPath = intent.getStringExtra("FILE_PATH")
                outputPath = File(inputPath).parent + File(inputPath).name.split(".")[0] + "(1)." + File(inputPath).extension
                startPosition = "00:00:00"
                endPosition = "00:02:00"

                //TRIM
                val command = arrayOf("-y", "-i", inputPath, "-ss", startPosition, "-to", endPosition, "-c", "copy", outputPath)

                // CROP
//                val command = arrayOf("-i", inputPath, "-filter:v", "crop=$width:$height:$x:$y", "-threads", "5", "-preset", "ultrafast", "-strict", "-2", "-c:a", "copy", outputPath)

//                val command = ""//TODO: command for video options.
                    try {
                        ff.execute(command, object : ExecuteBinaryResponseHandler() {
                            override fun onSuccess(message: String?) {
                                super.onSuccess(message)
                                Log.e(TAG, "onSuccess: " + message!!)
                            }

                            override fun onProgress(message: String?) {
                                super.onProgress(message)
                                Log.e(TAG, "onProgress: " + message!!)
                            }

                            override fun onFailure(message: String?) {
                                super.onFailure(message)
                                Log.e(TAG, "onFailure: " + message!!)
                            }

                            override fun onStart() {
                                super.onStart()
                                Log.e(TAG, "onStart: ")
                            }

                            override fun onFinish() {
                                super.onFinish()
                                Log.e(TAG, "onFinish: ")
                                loadVideo()
                            }
                        })
                    } catch (e: FFmpegCommandAlreadyRunningException) {
                    }
            }

            override fun onFailure() {
                Log.e("FFmpegLoad", "onFailure")
            }

            override fun onStart() {
            }
        })
    }

    private fun loadVideo() {
        val uri: Uri = Uri.parse(outputPath)

        videoView = findViewById(R.id.video_player)
        videoView.setVideoURI(uri)
        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        videoView.start()

    }
    companion object {
        val TAG = EditActivity::class.java.toString()
    }
}