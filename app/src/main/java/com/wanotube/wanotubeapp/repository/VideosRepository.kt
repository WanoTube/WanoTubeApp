package com.wanotube.wanotubeapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wanotube.wanotubeapp.database.VideosDatabase
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.domain.WanoTubeVideo
import com.wanotube.wanotubeapp.network.NetworkVideo
import com.wanotube.wanotubeapp.network.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.ServiceGenerator
import com.wanotube.wanotubeapp.network.VideoService
import com.wanotube.wanotubeapp.network.asDatabaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * Repository for fetching wanotube videos from the network and storing them on disk
 */
class VideosRepository(private val database: VideosDatabase) {
    val videos: LiveData<List<WanoTubeVideo>> = Transformations.map(database.videoDao.getVideos()) {
        it.asDomainModel()
    }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     */
    fun refreshVideos() {
        CoroutineScope(Dispatchers.IO).launch {
            val videoService: VideoService =
                ServiceGenerator.createService(VideoService::class.java, "auth-token")
            val responseBodyCall: Call<NetworkVideoContainer> = videoService.getVideos()
            responseBodyCall.enqueue(object : Callback<NetworkVideoContainer> {
                override fun onResponse(
                    call: Call<NetworkVideoContainer>?,
                    response: Response<NetworkVideoContainer?>?
                ) {

                    val videoModel = response?.body()?.asDatabaseModel()
                    CoroutineScope(Dispatchers.IO).launch {
                        if (videoModel != null) {
                            database.videoDao.insertAll(videoModel)
                        }
                    }
                }
                override fun onFailure(call: Call<NetworkVideoContainer>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }
    
    fun uploadVideo(title: MultipartBody.Part,
                    description: MultipartBody.Part,
                    video: MultipartBody.Part,
                    authorId: MultipartBody.Part,
                    duration: MultipartBody.Part,
                    privacy: MultipartBody.Part){
        CoroutineScope(Dispatchers.IO).launch {

            val videoService: VideoService =
                ServiceGenerator.createService(VideoService::class.java, "auth-token")
            val responseBodyCall: Call<NetworkVideo> = videoService.uploadVideo(
                title, 
                description, 
                video, 
                authorId, 
                duration,
                privacy)
            responseBodyCall.enqueue(object : Callback<NetworkVideo> {
                override fun onResponse(
                    call: Call<NetworkVideo>,
                    response: Response<NetworkVideo>
                ) {
                    Timber.e("Success %s", "success" +response.code())

//                    val playListModel = response?.body()?.asDatabaseModel()
//                    CoroutineScope(Dispatchers.IO).launch {
//                        if (playListModel != null) {
//                            database.videoDao.insertAll(playListModel)
//                        }
//                    }
                }
                override fun onFailure(call: Call<NetworkVideo>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }
}