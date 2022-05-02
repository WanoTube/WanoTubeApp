package com.wanotube.wanotubeapp.network

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() =
            "No network available, please check your WiFi or Data connection"
}

class NoInternetException() : IOException() {
    override val message: String
        get() =
            "No internet available, please check your connected WIFi or Data"
}

class NoServerException() : IOException() {
    override val message: String
        get() =
            "Server is not available, please try again"
}