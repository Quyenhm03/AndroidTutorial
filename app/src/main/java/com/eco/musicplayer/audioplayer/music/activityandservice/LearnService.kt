package com.eco.musicplayer.audioplayer.music.activityandservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class LearnService : Service() {

    private val TAG = "LearnService"
    private val binder = MyBinder()

    inner class MyBinder : Binder() {
        fun getService() : LearnService = this@LearnService
    }
    override fun onBind(p0: Intent?): IBinder {
        Log.d(TAG, "onBind called")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind called")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started via Intent")

        Thread {
            Thread.sleep(3000)
            val broadcastIntent = Intent("com.eco.musicplayer.audioplayer.music.activityandservice.MESSAGE")
            broadcastIntent.putExtra("msg", "Hello from Service via BroadcastReceiver!")
            sendBroadcast(broadcastIntent)
        }.start()

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        super.onDestroy()
    }

    fun sayHello(): String {
        return "Hello from Bound Service!"
    }
}