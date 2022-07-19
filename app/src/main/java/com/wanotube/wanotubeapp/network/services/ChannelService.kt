package com.wanotube.wanotubeapp.network.services

import com.wanotube.wanotubeapp.network.objects.*
import retrofit2.Call
import retrofit2.http.*

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

    @PATCH("channels/{id}/follow")
    fun followChannel(@Path("id") channelId: String): Call<NetworkFollow>?

    @PATCH("channels/{id}/unfollow")
    fun unfollowChannel(@Path("id") channelId: String): Call<NetworkFollow>?
    
    @GET("users/follow-info")
    fun getFollowInfo(): Call<NetworkFollow>?

    @GET("users/followings")
    fun getFollowingChannels(): Call<NetworkFollowingChannelContainer>?

    @GET("users/copyright-status")
    fun getCopyrightStatus(): Call<NetworkCopyrightStatus>?

    @PUT("users/update")
    fun updateUser(
        @Path("first_name") firstName: String,
        @Path("last_name") lastName: String,
        @Path("gender") gender: String,
        @Path("birth_date") birthDate: String,
        @Path("phone_number") phoneNumber: String,
        @Path("description") description: String,
        @Path("country") country: String
    ): Call<NetworkUser>?
}