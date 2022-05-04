package com.wanotube.wanotubeapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wanotube.wanotubeapp.database.VideosDatabase
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.NetworkVideo
import com.wanotube.wanotubeapp.network.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.ServiceGenerator
import com.wanotube.wanotubeapp.network.IVideoService
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
    val videos: LiveData<List<Video>> = Transformations.map(database.videoDao.getVideos()) {
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
            val videoService: IVideoService? =
                ServiceGenerator.createService(IVideoService::class.java, "auth-token")
            val responseBodyCall: Call<NetworkVideoContainer>? = videoService?.getVideos()
            responseBodyCall?.enqueue(object : Callback<NetworkVideoContainer> {
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
    
    fun getVideo(videoId: String): Call<NetworkVideo>? {
        val videoService: IVideoService? =
            ServiceGenerator.createService(IVideoService::class.java)
        return videoService?.getVideo(videoId)
    }
    
    fun uploadVideo(title: MultipartBody.Part,
                    size: MultipartBody.Part,
                    description: MultipartBody.Part,
                    video: MultipartBody.Part,
                    duration: MultipartBody.Part,
                    token: String): Call<NetworkVideo>? {
        val videoService: IVideoService? =
            ServiceGenerator.createService(IVideoService::class.java, token)
        return videoService?.uploadVideo(
            title,
            size,
            description,
            video,
            duration)
    }


    fun updateVideo(id: String,
                    title: String,
                    description: String,
                    url: String,
                    size: String,
                    duration: String,
                    visibility: String): Call<NetworkVideo>? {

        val idBody = MultipartBody.Part.createFormData("id", id)
        val titleBody = MultipartBody.Part.createFormData("title", title)
        val descriptionBody = MultipartBody.Part.createFormData("description", description)
        val urlBody = MultipartBody.Part.createFormData("url", url)
        val sizeBody = MultipartBody.Part.createFormData("size", size)
        val durationBody = MultipartBody.Part.createFormData("duration", duration)
        val visibilityBody = MultipartBody.Part.createFormData("visibility" ,visibility)

        val videoService: IVideoService? =
            ServiceGenerator.createService(IVideoService::class.java)
        return videoService?.updateVideo(
            idBody,
            titleBody,
            descriptionBody,
            urlBody,
            sizeBody,
            durationBody,
            visibilityBody)
    }
}