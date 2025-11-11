package com.eco.musicplayer.audioplayer.music.activityandservice

import android.app.IntentService
import android.content.Intent
import android.util.Log

class LearnIntentService : IntentService("LearnIntentService") {

    private val TAG = "LearnIntentService"

    override fun onHandleIntent(intent: Intent?) {
        val data = intent?.getStringExtra("data") ?: "No data"
        Log.d(TAG, "Process data: $data")

        Thread.sleep(3000)

        // process done, send result to Activity
        val broadcastIntent = Intent("com.eco.musicplayer.audioplayer.music.activityandservice.MESSAGE")
        broadcastIntent.putExtra("msg", "IntentService processed: $data")
        sendBroadcast(broadcastIntent)

        Log.d(TAG, "Task completed")
    }

}