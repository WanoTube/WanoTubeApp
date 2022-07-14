package com.wanotube.wanotubeapp.network.services

import com.wanotube.wanotubeapp.network.objects.LoginResult
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
    ): Call<LoginResult>

    @Multipart
    @POST("auth/register")
    fun signup(
        @Part email: MultipartBody.Part,
        @Part username: MultipartBody.Part,
        @Part firstName: MultipartBody.Part,
        @Part lastName: MultipartBody.Part,
        @Part phoneNumber: MultipartBody.Part,
//        @Part birthDate: MultipartBody.Part,
        @Part password: MultipartBody.Part,
    ): Call<LoginResult>
}