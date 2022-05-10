package com.wanotube.wanotubeapp.network.services

import com.wanotube.wanotubeapp.network.objects.NetworkAccount
import com.wanotube.wanotubeapp.network.objects.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.objects.UserResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface IChannelService {
    @Headers("isDisableAuthorization:true")
    @GET("users/{id}/")
    fun getUserByUserId(@Path("id") userId: String): Call<UserResult>?

    @Headers("isDisableAuthorization:true")
    @GET("channels/{id}/info")
    fun getChannel(@Path("id") channelId: String): Call<NetworkAccount>?
    
    @Headers("isDisableAuthorization:true")
    @GET("channels/{id}/videos")
    fun getPublicVideosByChannelId(@Path("id") channelId: String): Call<NetworkVideoContainer>?

    @GET("channels/videos")
    fun getAllVideosByChannelId(): Call<NetworkVideoContainer>?
}