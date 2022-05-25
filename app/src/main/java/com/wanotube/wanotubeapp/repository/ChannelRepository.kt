package com.wanotube.wanotubeapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wanotube.wanotubeapp.WanotubeApp.Companion.context
import com.wanotube.wanotubeapp.database.AppDatabase
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.network.services.IChannelService
import com.wanotube.wanotubeapp.network.objects.NetworkAccount
import com.wanotube.wanotubeapp.network.objects.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.ServiceGenerator
import com.wanotube.wanotubeapp.network.objects.UserResult
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ChannelRepository(private val database: AppDatabase) {
    val channels: LiveData<List<Account>> = Transformations.map(database.accountDao.getAccounts()) {
        it.asDomainModel()
    }

    fun addChannelByUserId(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val channelService: IChannelService? =
                ServiceGenerator.createService(IChannelService::class.java)

            val responseBodyCall: Call<UserResult>? = channelService?.getUserByUserId(userId)
            responseBodyCall?.enqueue(object : Callback<UserResult> {
                override fun onResponse(
                    call: Call<UserResult>?,
                    response: Response<UserResult?>?
                ) {
                    val userInfo = response?.body()
                    CoroutineScope(Dispatchers.IO).launch {
                        getChannel(channelService, userInfo)
                    }
                }
                override fun onFailure(call: Call<UserResult>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }
    
    fun getChannel(channelService: IChannelService, userInfo: UserResult?) {

        val channelId = userInfo?.channelId
        if (channelId != null) {

            val responseBodyCall: Call<NetworkAccount>? = channelService.getChannel(channelId)
            responseBodyCall?.enqueue(object : Callback<NetworkAccount> {
                override fun onResponse(
                    call: Call<NetworkAccount>?,
                    response: Response<NetworkAccount?>?
                ) {
                    var channelBodyModel = response?.body()?.asDatabaseModel()
                    if (channelBodyModel != null) {
                        channelBodyModel.avatar = userInfo.user?.avatar.toString()
                        CoroutineScope(Dispatchers.IO).launch {
                            database.accountDao.insert(channelBodyModel)
                        }
                    }
                }
                override fun onFailure(call: Call<NetworkAccount>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }

    suspend fun getPublicVideosByChannelId(channelId: String): Call<NetworkVideoContainer>? {
        return ServiceGenerator.createService(IChannelService::class.java)
            ?.getPublicVideosByChannelId(channelId)
    }
    
    suspend fun getAllVideosByChannelId(): Call<NetworkVideoContainer>? {
        val mAuthPreferences = context?.let { AuthPreferences(it) }
        if (mAuthPreferences != null) {
            mAuthPreferences.authToken?.let {
                return ServiceGenerator.createService(IChannelService::class.java, it)
                    ?.getAllVideosByChannelId()
            }
        }
        return null
    }
    
    fun clearVideos() {
        database.videoDao.clearVideos()
    }
}