package com.eco.musicplayer.audioplayer.music.activityandservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class MessageBroadcastReceiver(
    private val onMessageReceived: ((String) -> Unit)? = null
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val msg = intent?.getStringExtra("msg") ?: "No message"
        Log.d("Broadcast", "Broadcast received: $msg")

        onMessageReceived?.invoke(msg)

        Toast.makeText(context, "Received: $msg", Toast.LENGTH_LONG).show()
    }

}