package com.wanotube.wanotubeapp.network.authentication

interface IAuthenticatorService {
    /**
     * Tells the server to create the new user and return its auth token.
     * @param email
     * @param username
     * @param password
     * @return Access token
     */
    fun signUp(email: String?, username: String?, password: String?): String?

    /**
     * Logs the user in and returns its auth token.
     * @param email
     * @param password
     * @return Access token
     */
    fun login(email: String, password: String): String?
}
