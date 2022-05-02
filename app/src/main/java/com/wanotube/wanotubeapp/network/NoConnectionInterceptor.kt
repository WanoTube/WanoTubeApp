package com.wanotube.wanotubeapp.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wanotube.wanotubeapp.WanotubeApp
import com.wanotube.wanotubeapp.util.Constant
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class NoConnectionInterceptor: Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isNetworkAvailable()) {
            throw NoConnectivityException()
        } else if(!isInternetAvailable()) {
            throw NoInternetException()
        }
//        else if(!isServerAvailable()) {
//            throw NoServerException()
//        }
        else {
            chain.proceed(chain.request())
        }
    }
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager =
                WanotubeApp.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected && postAndroidMInternetCheck(connectivityManager)
        } catch (e: Exception) {
            false
        }
    }

    private fun postAndroidMInternetCheck(
        connectivityManager: ConnectivityManager
    ): Boolean {
        val network = connectivityManager.activeNetwork
        val connection =
            connectivityManager.getNetworkCapabilities(network)

        return connection != null && (
                connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun isServerAvailable(): Boolean {
        return try {
            val ipAddr = InetAddress.getByName("${Constant.BASE_URL}:${Constant.PORT}${Constant.VERSION}/")
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockAddr = InetSocketAddress("8.8.8.8", 53)

            sock.connect(sockAddr, timeoutMs)
            sock.close()

            true
        } catch (e: IOException) {
            false
        }
    }
}