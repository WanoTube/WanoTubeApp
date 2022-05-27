package com.wanotube.wanotubeapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.network.objects.NetworkVideo
import com.wanotube.wanotubeapp.network.objects.NetworkVideoContainer
import com.wanotube.wanotubeapp.repository.ChannelRepository
import com.wanotube.wanotubeapp.repository.VideosRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
 * WanoTubeViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
class WanoTubeViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * The data source this ViewModel will fetch results from.
     */
    private val videosRepository = VideosRepository(getDatabase(application))
    private val channelRepository = ChannelRepository(getDatabase(application))
    
    /**
     * A playlist of videos displayed on the screen.
     */
    val allPublicVideos = videosRepository.videos
    val channels = channelRepository.channels
    var watchLaterList: MutableLiveData<List<Video>> = MutableLiveData<List<Video>>()

    var isInitialized = false

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    init {
        getWatchLaterList()
    }
    
    fun clearDataFromRepository() {
        viewModelScope.launch {
            videosRepository.clearVideos()
        }
    }
    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                videosRepository.refreshVideos(channelRepository)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(allPublicVideos.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    fun getWatchLaterList() {
        val response = videosRepository.getWatchLaterList()
        response?.enqueue(object : Callback<NetworkVideoContainer> {
            override fun onResponse(
                call: Call<NetworkVideoContainer>?,
                response: Response<NetworkVideoContainer?>?,
            ) {
                if (response?.code() == 200) {
                    watchLaterList.value = response.body()?.asDatabaseModel()?.asDomainModel()!!
                }
            }
            override fun onFailure(call: Call<NetworkVideoContainer>?, t: Throwable?) {
                Timber.e("Failed: error: %s", t.toString())
            }
        })
    }
    
    /**
     * Factory for constructing WanoTubeViewModel with parameter
     */
    class WanoTubeViewModelFactory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WanoTubeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WanoTubeViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
