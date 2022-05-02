package com.wanotube.wanotubeapp.network.authentication

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

class AuthenticationInterceptor(private val authToken: String) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val isDisableAuthorization = original.header("isDisableAuthorization") == "true"
        Timber.e("isDisableAuthorization: $isDisableAuthorization")
        return if (!isDisableAuthorization) {
            val builder: Request.Builder = original.newBuilder()
                .header("authorization", authToken)
            val request: Request = builder.build()
            chain.proceed(request)
        } else {
            chain.proceed(original)
        }
    }
}