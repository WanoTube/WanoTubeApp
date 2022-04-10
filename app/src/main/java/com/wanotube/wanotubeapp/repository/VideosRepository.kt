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
                        id = "Video1",
                        title = "luyện nghe tiếng trung",
                        description = "description",
                        url = "https://www.youtube.com/watch?v=n2B22TfO3CM",
                        updated = "Test",
                        thumbnail = "Test",
                        size = 0,
                        totalViews = 200,
                        totalLikes = 100,
                        totalComments = 2,
                        duration = "2:30",
                        authorId = "User",
                        visibility = 1
                    ),
                    NetworkVideo(
                        id = "Video2",
                        title = "INTO1—《See You》",
                        description = "description",
                        url = "https://www.youtube.com/watch?v=XSZ1nAohAqY",
                        updated = "Test",
                        thumbnail = "Test",
                        size = 0,
                        totalViews = 200,
                        totalLikes = 100,
                        totalComments = 2,
                        duration = "2:30",
                        authorId = "User",
                        visibility = 1
                    ),
                    NetworkVideo(
                        id = "Video3",
                        title = "INTO1刘宇—《文刀刘》",
                        description = "description",
                        url = "https://www.youtube.com/watch?v=lLogPNqrBHs",
                        updated = "Test",
                        thumbnail = "Test",
                        size = 0,
                        totalViews = 200,
                        totalLikes = 100,
                        totalComments = 2,
                        duration = "2:30",
                        authorId = "User",
                        visibility = 1
                    ),
                    NetworkVideo(
                        id = "Video4",
                        title = "【LiuYu & Zhou Keyu】【ENG SUB】I love you,",
                        description = "description",
                        url = "https://www.youtube.com/watch?v=hwFw69ltwww",
                        updated = "Test",
                        thumbnail = "Test",
                        size = 0,
                        totalViews = 200,
                        totalLikes = 100,
                        totalComments = 2,
                        duration = "2:30",
                        authorId = "User",
                        visibility = 1
                    ),
                    NetworkVideo(
                        id = "Video5",
                        title = "INTO1 [zhou keyu and Liuyu] ",
                        description = "description",
                        url = "https://www.youtube.com/watch?v=ps7Z4a0ANSk",
                        updated = "Test",
                        thumbnail = "Test",
                        size = 0,
                        totalViews = 200,
                        totalLikes = 100,
                        totalComments = 2,
                        duration = "2:30",
                        authorId = "User",
                        visibility = 1
                    ),
                    NetworkVideo(
                        id = "Video6",
                        title = "BRTV Spring Festival Gala LIU YU",
                        description = "description",
                        url = "https://www.youtube.com/watch?v=nIqKSdC_RaU",
                        updated = "Test",
                        thumbnail = "Test",
                        size = 0,
                        totalViews = 200,
                        totalLikes = 100,
                        totalComments = 2,
                        duration = "2:30",
                        authorId = "User",
                        visibility = 1
                    )
                )
            )

            val playListModel = playlist.asDatabaseModel()
            database.videoDao.insertAll(playListModel)
        }
    }
}