package com.wanotube.wanotubeapp.network.authentication

import com.wanotube.wanotubeapp.network.IUserService
import com.wanotube.wanotubeapp.network.LoginResult
import com.wanotube.wanotubeapp.network.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.Collections

class MyAuthenticatorService : IAuthenticatorService {
    companion object {
        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private var mCredentialsRepo: Map<String, String>? = null

        init {
            val credentials: MutableMap<String, String> = HashMap()
            credentials["demo@example.com"] = "demo"
            credentials["foo@example.com"] = "foobar"
            credentials["user@example.com"] = "pass"
            mCredentialsRepo = Collections.unmodifiableMap(credentials)
        }
    }

    override fun signUp(email: String?, username: String?, password: String?): String? {
        // TODO: register new user on the server and return its auth token
        return null
    }

    override fun login(email: String, password: String): String? {
        var authToken: String? = null
        CoroutineScope(Dispatchers.IO).launch {
            val userService: IUserService =
                ServiceGenerator.createService(IUserService::class.java)

            val emailBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("email", email)
            val passwordBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("password", password)

            val responseBodyCall: Call<LoginResult> = userService.login(emailBodyPart, passwordBodyPart)
            responseBodyCall.enqueue(object : Callback<LoginResult> {
                override fun onResponse(
                    call: Call<LoginResult>?,
                    response: Response<LoginResult?>?,
                ) {
                    val body = response?.body()
                    if (body == null) {
                        //Error 
                    } else {
                        authToken = body.token
                    }
                }
                override fun onFailure(call: Call<LoginResult>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
        return authToken
    }
}
