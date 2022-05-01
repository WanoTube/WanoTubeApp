package com.wanotube.wanotubeapp.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IUserService {
    @Multipart
    @POST("auth/login")
    fun login(
        @Part email: MultipartBody.Part,
        @Part password: MultipartBody.Part,
    ): Call<NetworkUser>
}