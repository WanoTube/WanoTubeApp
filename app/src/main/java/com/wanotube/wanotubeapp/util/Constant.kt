package com.wanotube.wanotubeapp.util

object Constant {
    const val REQUEST_VIDEO = 3
    const val BASE_URL = "http://localhost"
    const val PORT = 8000
    const val VERSION = "/v1"

    const val FULL_URL = "${BASE_URL}:${PORT}${VERSION}/"
    const val URL = "${BASE_URL}:${PORT}/"
    
    const val PRODUCTION_WEB_URL = "https://www.demo.o-redsky.xyz"

//    const val FULL_URL = "https://api.demo.o-redsky.xyz/v1/"
//    const val URL = FULL_URL
    
    const val TIME_OUT = 2500L
}