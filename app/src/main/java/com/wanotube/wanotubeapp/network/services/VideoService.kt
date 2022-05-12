package com.wanotube.wanotubeapp.network.services

import com.wanotube.wanotubeapp.network.objects.NetworkVideo
import com.wanotube.wanotubeapp.network.objects.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.objects.NetworkVideoWatch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface IVideoService {
    @Headers("isDisableAuthorization:true")
    @GET("videos/public")
    fun getVideos(): Call<NetworkVideoContainer>?

    @GET("videos/{id}")
    fun getVideo(@Path("id") id: String): Call<NetworkVideoWatch>?

    @Multipart
    @POST("videos/upload")
    fun uploadVideo(
        @Part title: MultipartBody.Part,
        @Part size: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part video: MultipartBody.Part,
        @Part duration: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        ): Call<NetworkVideo>?

    @Headers("isDisableAuthorization:true")
    @Multipart
    @PATCH("videos/update")
    fun updateVideo(
        @Part id: MultipartBody.Part,
        @Part title: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part url: MultipartBody.Part,
        @Part size: MultipartBody.Part,
        @Part duration: MultipartBody.Part,
        @Part visibility: MultipartBody.Part,
        ): Call<NetworkVideo>?

    @Headers("isDisableAuthorization:true")
    @Multipart
    @POST("videos/like")
    fun likeVideo(@Part targetId: MultipartBody.Part): Call<NetworkVideo>?
}