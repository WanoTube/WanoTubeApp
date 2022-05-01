package com.wanotube.wanotubeapp.network

import com.google.gson.GsonBuilder
import com.wanotube.wanotubeapp.util.Constant.PORT
import com.wanotube.wanotubeapp.util.Constant.SYSTEM_URL
import com.wanotube.wanotubeapp.util.Constant.VERSION
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface IVideoService {
    @GET("videos")
    fun getVideos(): Call<NetworkVideoContainer>

    @GET("videos/")
    fun getVideo(@Query("id") id: String): Call<NetworkVideo>

    @Multipart
    @POST("videos/upload")
    fun uploadVideo(
        @Part title: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part video: MultipartBody.Part,
        @Part author_id: MultipartBody.Part,
        @Part duration: MultipartBody.Part,
        @Part privacy: MultipartBody.Part
        ): Call<NetworkVideo>
}
//
///**
// * Main entry point for network access. Call like `WanoTubeNetwork.wanotubes.getVideos()`
// */
//object WanoTubeNetwork {
//
//    // Configure retrofit to parse JSON and use coroutines
//    private val retrofit = Retrofit.Builder()
//        .baseUrl("$SYSTEM_URL:$PORT$VERSION/")
//        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
//        .build()
//
//    val wanotubes: WanoTubeService = retrofit.create(WanoTubeService::class.java)
//}
