package com.wanotube.wanotubeapp.repository

import com.wanotube.wanotubeapp.database.VideosDatabase
import com.wanotube.wanotubeapp.domain.User
import com.wanotube.wanotubeapp.network.ServiceGenerator
import com.wanotube.wanotubeapp.network.IUserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import timber.log.Timber

class UserRepository(private val database: VideosDatabase) {

    fun login() {
        CoroutineScope(Dispatchers.IO).launch {
            val userService: IUserService =
                ServiceGenerator.createService(IUserService::class.java, "auth-token")
            val call: Call<User?>? = userService.login()
            val user = call?.execute()?.body()
            Timber.e("Ngan %s", "User: $user")
        }
    }
}