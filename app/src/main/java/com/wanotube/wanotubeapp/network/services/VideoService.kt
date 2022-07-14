package com.wanotube.wanotubeapp.network.services

import com.wanotube.wanotubeapp.network.objects.NetworkVideo
import com.wanotube.wanotubeapp.network.objects.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.objects.NetworkVideoWatch
import com.wanotube.wanotubeapp.network.objects.NetworkVideoWatchContainer
import com.wanotube.wanotubeapp.network.objects.NetworkWatchHistoryContainer
import com.wanotube.wanotubeapp.network.objects.NetworkWatchHistoryDate
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

    @Headers("isDisableAuthorization:true")
    @GET("videos/{id}")
    fun getVideo(@Path("id") id: String): Call<NetworkVideoWatch>?

    @GET("videos/{id}")
    fun getVideoWithAuthorization(@Path("id") id: String): Call<NetworkVideoWatch>?

    @Multipart
    @POST("videos/delete")
    fun deleteVideo(@Part id: MultipartBody.Part): Call<NetworkVideo>?

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
        @Part tags: MultipartBody.Part
        ): Call<NetworkVideo>?

    @POST("videos/like/{id}")
    fun likeVideo(@Path("id") videoId: String): Call<NetworkVideo>?

    @PATCH("videos/watch-later/{videoId}")
    fun watchLater(@Path("videoId") videoId: String): Call<NetworkVideo>?

    @PATCH("videos/watch-later/{videoId}/remove")
    fun removeWatchLater(@Path("videoId") videoId: String): Call<NetworkVideo>?

    @GET("videos/watch-later")
    fun getWatchLaterVideos(): Call<NetworkVideoWatchContainer>?

    @GET("videos/history")
    fun getWatchHistoryVideos(): Call<NetworkWatchHistoryContainer>?

    @PATCH("videos/{id}/view")
    fun increaseView(@Path("id") videoId: String): Call<NetworkWatchHistoryDate>?
}