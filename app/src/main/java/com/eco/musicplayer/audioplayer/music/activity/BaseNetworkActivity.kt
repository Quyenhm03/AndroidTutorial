package com.eco.musicplayer.audioplayer.music.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.network.NetworkMonitor

open class BaseNetworkActivity : AppCompatActivity() {

    private var currentToast: Toast? = null
    private var lastToastState: Boolean? = null

    private val networkListener: (Boolean) -> Unit = { connected ->
        Log.d("Base", Thread.currentThread().toString())
        runOnUiThread {
            if (lastToastState != connected) {
                lastToastState = connected
                showNetworkStatus(connected)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkMonitor.addListener(networkListener)
    }

    override fun onDestroy() {
        NetworkMonitor.removeListener(networkListener)
        currentToast?.cancel()
        super.onDestroy()
    }

    private fun showNetworkStatus(isConnected: Boolean) {
        currentToast?.cancel()
        val message = if (isConnected) "Internet connected" else "Internet disconnected"
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
}