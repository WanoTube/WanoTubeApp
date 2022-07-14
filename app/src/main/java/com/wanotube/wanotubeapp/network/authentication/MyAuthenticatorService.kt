package com.wanotube.wanotubeapp.network.authentication

import com.wanotube.wanotubeapp.network.services.IUserService
import com.wanotube.wanotubeapp.network.objects.LoginResult
import com.wanotube.wanotubeapp.network.ServiceGenerator
import okhttp3.MultipartBody
import retrofit2.Call

class MyAuthenticatorService : IAuthenticatorService {
    override fun signUp(
        email: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String): Call<LoginResult>? {
        // TODO: register new user on the server and return its auth token
        val userService: IUserService? =
            ServiceGenerator.createService(IUserService::class.java)

        val emailBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("email", email)
        val usernameBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("username", username)
        val passwordBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("password", password)
        val firstNameBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("first_name", firstName)
        val lastNameBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("last_name", lastName)
        val phoneNumberBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("phone_number", phoneNumber)
//        val birthDateBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("birth_date", birthDate)

        return userService?.signup(
            emailBodyPart,
            usernameBodyPart,
            firstNameBodyPart,
            lastNameBodyPart,
            phoneNumberBodyPart,
//            birthDateBodyPart,
            passwordBodyPart)
    }

    override suspend fun login(email: String, password: String): Call<LoginResult>? {
        val userService: IUserService? =
            ServiceGenerator.createService(IUserService::class.java)

        val emailBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("email", email)
        val passwordBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("password", password)

        return userService?.login(emailBodyPart, passwordBodyPart)
    }
}
