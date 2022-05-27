package com.wanotube.wanotubeapp.repository

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wanotube.wanotubeapp.WanotubeApp.Companion.context
import com.wanotube.wanotubeapp.database.AppDatabase
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.database.entity.DatabaseVideo
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.objects.NetworkVideo
import com.wanotube.wanotubeapp.network.objects.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.ServiceGenerator
import com.wanotube.wanotubeapp.network.services.IVideoService
import com.wanotube.wanotubeapp.network.objects.NetworkVideoWatch
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.network.objects.NetworkWatchHistoryDate
import com.wanotube.wanotubeapp.util.VideoType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File

/**
 * Repository for fetching wanotube videos from the network and storing them on disk
 */
class VideosRepository(private val database: AppDatabase) {
    //All public videos
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
    fun refreshVideos(channelRepository: ChannelRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            val videoService: IVideoService? =
                ServiceGenerator.createService(IVideoService::class.java)
            val responseBodyCall: Call<NetworkVideoContainer>? = videoService?.getVideos()
            responseBodyCall?.enqueue(object : Callback<NetworkVideoContainer> {
                override fun onResponse(
                    call: Call<NetworkVideoContainer>?,
                    response: Response<NetworkVideoContainer?>?
                ) {

                    val videoModel = response?.body()?.asDatabaseModel()
                    CoroutineScope(Dispatchers.IO).launch {
                        if (videoModel != null) {
                            withContext(Dispatchers.Main) {
                                videoModel.forEach {
                                    channelRepository.addChannelByUserId(it.authorId)
                                }
                            }
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
    
    fun insertAllVideos(videosModel:  List<DatabaseVideo>?) {
        if (videosModel != null) {
            database.videoDao.insertAll(videosModel)
        }
    }
    
    fun getVideo(videoId: String): Call<NetworkVideoWatch>? {
        val videoService: IVideoService? =
            ServiceGenerator.createService(IVideoService::class.java)
        return videoService?.getVideo(videoId)
    }

    fun getVideoWithAuthorization(videoId: String): Call<NetworkVideoWatch>? {
        val mAuthPreferences = context?.let { AuthPreferences(it) }
        mAuthPreferences?.authToken?.let {
            Timber.e("Token: %s", it)
            val videoService: IVideoService? =
                ServiceGenerator.createService(IVideoService::class.java, it)
            return videoService?.getVideoWithAuthorization(videoId)
        }
        return null
    }
    
    fun insertVideoToDatabase(video: DatabaseVideo) {
        database.videoDao.insert(video)
    }
    
    private fun setDuration(file: File): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.fromFile(file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillis = time.toFloat()
        retriever.release()
        return (timeInMillis*1000).toInt()
    }
    
    fun uploadVideo(file: File,
                    token: String,
                    isUploadNormalVideo: Boolean): Call<NetworkVideo>? {
        val videoBody: RequestBody = file.asRequestBody("video/*".toMediaTypeOrNull())

        val video = MultipartBody.Part.createFormData(
                "video",
                file.name,
                videoBody
            )

        val title = MultipartBody.Part.createFormData(
                "title",
                file.name
            )

        val size = MultipartBody.Part.createFormData(
                "size",
//                (file.length() / 1024).toString() //KB
                file.length().toString()//Byte
            )

        val description = MultipartBody.Part.createFormData(
                "description",
                ""
            )

        val duration = MultipartBody.Part.createFormData(
                "duration", setDuration(file).toString())

        val videoType = if (isUploadNormalVideo) VideoType.NORMAL
                        else VideoType.SHORT
        
        val type = MultipartBody.Part.createFormData(
                "type", videoType.name)

        val videoService: IVideoService? =
            ServiceGenerator.createService(IVideoService::class.java, token)
        
        return videoService?.uploadVideo(
            title,
            size,
            description,
            video,
            duration,
            type
        )
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

    fun likeVideo(targetId: String) {
        val targetIdBody = MultipartBody.Part.createFormData("target_id", targetId)
        val videoService: IVideoService? =
            ServiceGenerator.createService(IVideoService::class.java)
        val response = videoService?.likeVideo(targetIdBody)
        response?.enqueue(object : Callback<NetworkVideo> {
            override fun onResponse(
                call: Call<NetworkVideo>?,
                response: Response<NetworkVideo?>?
            ) {
                val videoModel = response?.body()?.asDatabaseModel()
                CoroutineScope(Dispatchers.IO).launch {
                    if (videoModel != null) {
                        database.videoDao.likeVideo(videoModel.totalLikes, videoModel.id)
                    }
                }
            }
            override fun onFailure(call: Call<NetworkVideo>?, t: Throwable?) {
                Timber.e("Failed: error: %s", t.toString())
            }
        })
    }
    
    fun clearVideos() {
        database.videoDao.clearVideos()
    }
    
    fun getVideoFromDatabase(videoId: String): LiveData<DatabaseVideo>? {
        return database.videoDao.getVideo(videoId)
    }

    fun watchLater(videoId: String): Call<NetworkVideo>? {
        val mAuthPreferences = context?.let { AuthPreferences(it) }
        mAuthPreferences?.authToken?.let {
            val response = ServiceGenerator.createService(IVideoService::class.java, it)?.watchLater(videoId)
            response?.enqueue(object : Callback<NetworkVideo> {
                override fun onResponse(
                    call: Call<NetworkVideo>?,
                    response: Response<NetworkVideo?>?,
                ) {
                    if (response?.code() == 200) {
                        Timber.e("Added to watch later")
                    } else {
                        Timber.e("Cannot add to watch later")
                    }
                }
                override fun onFailure(call: Call<NetworkVideo>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
        return null
    }
    
    fun removeWatchLater(videoId: String) {
        val mAuthPreferences = context?.let { AuthPreferences(it) }
        mAuthPreferences?.authToken?.let {
            val response = ServiceGenerator.createService(IVideoService::class.java, it)?.removeWatchLater(videoId)
            response?.enqueue(object : Callback<NetworkVideo> {

                override fun onResponse(
                    call: Call<NetworkVideo>?,
                    response: Response<NetworkVideo?>?,
                ) {
                    if (response?.code() == 200) {
                        Timber.e("Removed watch later")
                    } else {
                        Timber.e("Cannot remove this watch later")
                    }
                }
                override fun onFailure(call: Call<NetworkVideo>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }

    fun getWatchLaterList(): Call<NetworkVideoContainer>? {
        return ServiceGenerator.createService(IVideoService::class.java)?.getWatchLaterVideos()
    }
    
    fun increaseView(videoId: String): Call<NetworkWatchHistoryDate>? {
        Timber.e("increase view for video ($videoId)")
        val mAuthPreferences = context?.let { AuthPreferences(it) }
        mAuthPreferences?.authToken?.let {
            val videoService: IVideoService? =
                ServiceGenerator.createService(IVideoService::class.java, it)
            val response = videoService?.increaseView(videoId)
            response?.enqueue(object : Callback<NetworkWatchHistoryDate> {

                override fun onResponse(
                    call: Call<NetworkWatchHistoryDate>?,
                    response: Response<NetworkWatchHistoryDate?>?,
                ) {
                    if (response?.code() == 200) {
                        Timber.e("Increased view")
                    } else {
                        Timber.e("Cannot increase view")
                    }
                }
                override fun onFailure(call: Call<NetworkWatchHistoryDate>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
        return null
    }
}