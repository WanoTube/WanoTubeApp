package com.wanotube.wanotubeapp.network

import com.wanotube.wanotubeapp.domain.User
import retrofit2.Call
import retrofit2.http.POST

interface IUserService {
    @POST("/login")
    fun login(): Call<User?>?
}