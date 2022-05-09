package com.wanotube.wanotubeapp.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ICommentService {
    @Headers("isDisableAuthorization:true")
    @GET("videos/{id}/comments")
    fun getCommentsByVideoId(@Path("id") videoId: String): Call<NetworkCommentContainer>?

    @Multipart
    @POST("videos/comment")
    fun sendComment(
        @Part content: MultipartBody.Part,
        @Part videoId: MultipartBody.Part,
    ): Call<NetworkComment>?
}