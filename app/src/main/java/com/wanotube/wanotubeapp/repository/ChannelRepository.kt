package com.wanotube.wanotubeapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wanotube.wanotubeapp.database.AppDatabase
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.network.IChannelService
import com.wanotube.wanotubeapp.network.NetworkAccount
import com.wanotube.wanotubeapp.network.ServiceGenerator
import com.wanotube.wanotubeapp.network.UserResult
import com.wanotube.wanotubeapp.network.asDatabaseModel
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
}