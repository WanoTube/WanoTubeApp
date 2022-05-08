package com.wanotube.wanotubeapp.network

import android.text.TextUtils
import com.wanotube.wanotubeapp.network.authentication.AuthenticationInterceptor
import com.wanotube.wanotubeapp.util.Constant.BASE_URL
import com.wanotube.wanotubeapp.util.Constant.PORT
import com.wanotube.wanotubeapp.util.Constant.VERSION
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit


object ServiceGenerator {
    private val builder = Retrofit.Builder()
        .baseUrl("$BASE_URL:$PORT$VERSION/")
        .addConverterFactory(GsonConverterFactory.create())
    
    private var retrofit = builder.build()
    
//    private val logging = HttpLoggingInterceptor()
//        .setLevel(HttpLoggingInterceptor.Level.BODY)
    
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
    
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
    
    fun <S> createService(serviceClass: Class<S>?): S? {
        return createService(serviceClass, null)
    }

    fun <S> createService(
        serviceClass: Class<S>?, authToken: String?,
    ): S? {
        try {
//            val noConnectionInterceptor = NoConnectionInterceptor()
//            if (!httpClient.interceptors().contains(noConnectionInterceptor as Interceptor)) {
//                httpClient.addInterceptor(noConnectionInterceptor)
//            }
            
            if (!TextUtils.isEmpty(authToken) && authToken != null) {
                val authInterceptor = AuthenticationInterceptor(authToken)
                if (!httpClient.interceptors().contains(authInterceptor as Interceptor)) {
                    httpClient.addInterceptor(authInterceptor)
                }
            }
            
//            val serverErrorInterception = ServerErrorInterception()
//            if (!httpClient.interceptors().contains(serverErrorInterception as Interceptor)) {
//                httpClient.addInterceptor(serverErrorInterception)
//            }

            builder.client(httpClient.build())
            retrofit = builder.build()
            return retrofit.create(serviceClass)
        } catch (error: Exception) {
            Timber.e("Error: %s", error.message)
            return null
        }
    }
}