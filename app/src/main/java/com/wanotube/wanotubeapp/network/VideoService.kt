package com.wanotube.wanotubeapp.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface IVideoService {
    @GET("videos/public")
    fun getVideos(): Call<NetworkVideoContainer>

    @GET("videos/")
    fun getVideo(@Query("id") id: String): Call<NetworkVideo>

    @Headers("isAuthenticated=false")
    @Multipart
    @POST("videos/upload")
    fun uploadVideo(
        @Part title: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part video: MultipartBody.Part,
        @Part duration: MultipartBody.Part,
        @Part privacy: MultipartBody.Part
        ): Call<NetworkVideo>
}