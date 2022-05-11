package com.wanotube.wanotubeapp.network.authentication

import com.wanotube.wanotubeapp.network.services.IUserService
import com.wanotube.wanotubeapp.network.objects.LoginResult
import com.wanotube.wanotubeapp.network.ServiceGenerator
import okhttp3.MultipartBody
import retrofit2.Call

class MyAuthenticatorService : IAuthenticatorService {
    override fun signUp(email: String?, username: String?, password: String?): String? {
        // TODO: register new user on the server and return its auth token
        return null
    }

    override suspend fun login(email: String, password: String): Call<LoginResult>? {
        val userService: IUserService? =
            ServiceGenerator.createService(IUserService::class.java)

        val emailBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("email", email)
        val passwordBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("password", password)

        return userService?.login(emailBodyPart, passwordBodyPart)
    }
}
