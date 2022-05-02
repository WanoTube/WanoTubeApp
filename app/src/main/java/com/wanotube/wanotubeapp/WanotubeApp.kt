package com.wanotube.wanotubeapp

import android.app.Application
import android.content.Context

class WanotubeApp : Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    companion object {
        var instance: WanotubeApp? = null
            private set
        val context: Context?
            get() = instance
    }
}