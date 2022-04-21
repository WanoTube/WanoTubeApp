package com.wanotube.wanotubeapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wanotube.wanotubeapp.database.VideosDatabase
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.domain.WanoTubeVideo
import com.wanotube.wanotubeapp.network.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.WanoTubeNetwork
import com.wanotube.wanotubeapp.network.asDatabaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            val playlist = WanoTubeNetwork.wanotubes.getVideos()
            playlist.enqueue(object : Callback<NetworkVideoContainer> {
                override fun onResponse(
                    call: Call<NetworkVideoContainer>?,
                    response: Response<NetworkVideoContainer?>?
                ) {
                    //TODO Xử ký dữ liệu trả về

                    val playListModel = response?.body()?.asDatabaseModel()
                    CoroutineScope(Dispatchers.IO).launch {
                        if (playListModel != null) {
                            database.videoDao.insertAll(playListModel)
                        }
                    }
                }
                override fun onFailure(call: Call<NetworkVideoContainer>?, t: Throwable?) {
                    Timber.e("Failed: t: %s", t.toString())
                }
            })
        }
    }
}