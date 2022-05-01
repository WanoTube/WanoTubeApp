package com.wanotube.wanotubeapp.network.authentication

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class AuthenticationInterceptor(private val authToken: String) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val builder: Request.Builder = original.newBuilder()
            .header("authorization", authToken)
        val request: Request = builder.build()
        return chain.proceed(request)
    }
}