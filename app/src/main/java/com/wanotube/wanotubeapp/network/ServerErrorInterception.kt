package com.wanotube.wanotubeapp.network

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.wanotube.wanotubeapp.WanotubeApp.Companion.context
import okhttp3.Interceptor
import okhttp3.Response

class ServerErrorInterception: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        when (response.code) {
            401, 402 -> {
                Handler(Looper.getMainLooper()).post{
                    Toast.makeText(context, VerifyException().message, Toast.LENGTH_SHORT).show()
                }
            }
            //TODO: Handle other status code
        }
        return response
    }
}