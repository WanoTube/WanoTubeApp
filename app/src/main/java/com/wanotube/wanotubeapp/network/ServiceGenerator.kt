package com.wanotube.wanotubeapp.network

import android.text.TextUtils
import com.wanotube.wanotubeapp.network.authentication.AuthenticationInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceGenerator {
    private const val BASE_URL = "http://localhost"
    private const val PORT = 8000
    private const val VERSION = "/v1"

    private val builder = Retrofit.Builder()
        .baseUrl("$BASE_URL:$PORT$VERSION/")
        .addConverterFactory(GsonConverterFactory.create())
    
    private var retrofit = builder.build()
    
//    private val logging = HttpLoggingInterceptor()
//        .setLevel(HttpLoggingInterceptor.Level.BODY)
    
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    
//    fun <S> createService(
//        serviceClass: Class<S>?,
//    ): S {
//        if (!httpClient.interceptors().contains(logging)) {
//            httpClient.addInterceptor(logging)
//            builder.client(httpClient.build())
//            retrofit = builder.build()
//        }
//        return retrofit.create(serviceClass)
//    }
    
    fun <S> createService(serviceClass: Class<S>?): S {
        return createService(serviceClass, null)
    }

    fun <S> createService(
        serviceClass: Class<S>?, authToken: String?,
    ): S {
        if (!TextUtils.isEmpty(authToken)) {
            val interceptor = authToken?.let { AuthenticationInterceptor(it) }
            if (!httpClient.interceptors().contains(interceptor as Interceptor)) {
                httpClient.addInterceptor(interceptor)
                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }
        return retrofit.create(serviceClass)
    }
}