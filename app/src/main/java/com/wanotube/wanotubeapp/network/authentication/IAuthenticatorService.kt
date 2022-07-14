package com.wanotube.wanotubeapp.network.authentication

import com.wanotube.wanotubeapp.network.objects.LoginResult
import retrofit2.Call

interface IAuthenticatorService {
    /**
     * Tells the server to create the new user and return its auth token.
     * @param email
     * @param username
     * @param password
     * @return Access token
     */
    fun signUp(
        email: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,)
    :Call<LoginResult>?

    /**
     * Logs the user in and returns its auth token.
     * @param email
     * @param password
     * @return Access token
     */
    suspend fun login(email: String, password: String): Call<LoginResult>?
}
