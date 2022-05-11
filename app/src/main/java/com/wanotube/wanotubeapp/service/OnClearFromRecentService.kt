package com.wanotube.wanotubeapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber


class OnClearFromRecentService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("ClearFromRecentService %s", "Service Started")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ClearFromRecentService %s", "Service Destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.e("ClearFromRecentService %s", "END")
        //Code here
        stopSelf()
    }
}