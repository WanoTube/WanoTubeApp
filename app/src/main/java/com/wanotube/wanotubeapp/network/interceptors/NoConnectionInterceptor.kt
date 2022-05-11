package com.wanotube.wanotubeapp.network.interceptors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.wanotube.wanotubeapp.WanotubeApp.Companion.context
import com.wanotube.wanotubeapp.util.Constant.FULL_URL
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


class NoConnectionInterceptor: Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isNetworkAvailable()) {
            Handler(Looper.getMainLooper()).post{
                Toast.makeText(context, NoConnectivityException().message, Toast.LENGTH_SHORT).show()
            }
            throw NoConnectivityException()
        } else if(!isInternetAvailable()) {
            Handler(Looper.getMainLooper()).post{
                Toast.makeText(context, NoInternetException().message, Toast.LENGTH_SHORT).show()
            }
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
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected && postAndroidMInternetCheck(connectivityManager)
        } catch (e: Exception) {
            false
        }
    }

    private fun postAndroidMInternetCheck(
        connectivityManager: ConnectivityManager,
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
            val ipAddr = InetAddress.getByName(FULL_URL)
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