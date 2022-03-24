package com.wanotube.wanotubeapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wanotube.wanotubeapp.database.VideosDatabase
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.domain.WanoTubeVideo
import com.wanotube.wanotubeapp.network.NetworkVideo
import com.wanotube.wanotubeapp.network.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.WanoTubeNetwork
import com.wanotube.wanotubeapp.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
//            val playlist = WanoTubeNetwork.wanotubes.getPlaylist()
            val playlist = NetworkVideoContainer(
                videos = listOf(
                    NetworkVideo(
                        id = "Bye",
                        title = "Test",
                        description = "description",
                        url = "https://www.youtube.com/watch?v=n2B22TfO3CM",
                        updated = "Test",
                        thumbnail = "Test",
                        closedCaptions = "closedCaptions"
                    )
                )
            )
            Log.e("Repo", "playlist: $playlist")

            val playListModel = playlist.asDatabaseModel()
            Log.e("Repo", "playListModel: $playListModel")
            database.videoDao.insertAll(playListModel)
        }
    }
}