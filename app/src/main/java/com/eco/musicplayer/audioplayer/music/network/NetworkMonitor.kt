package com.eco.musicplayer.audioplayer.music.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

object NetworkMonitor {

    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val listeners = mutableSetOf<(Boolean) -> Unit>()
    private lateinit var appContext: Context

    private var lastKnownState: Boolean? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        lastKnownState = isConnected()

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                checkAndNotify()
            }

            override fun onLost(network: Network) {
                checkAndNotify()
            }

            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                checkAndNotify()
            }
        }

        connectivityManager?.registerNetworkCallback(request, networkCallback!!)

        notifyIfChanged(lastKnownState!!)
    }

    private fun checkAndNotify() {
        val current = isConnected()
        if (current != lastKnownState) {
            lastKnownState = current
            notifyAll(current)
        }
    }

    private fun notifyIfChanged(isConnected: Boolean) {
        if (lastKnownState == null || isConnected != lastKnownState) {
            lastKnownState = isConnected
            notifyAll(isConnected)
        }
    }

    private fun notifyAll(isConnected: Boolean) {
        listeners.forEach { it(isConnected) }
    }

    fun addListener(listener: (Boolean) -> Unit) {
        listeners.add(listener)
        lastKnownState?.let { listener(it) }
    }

    fun removeListener(listener: (Boolean) -> Unit) {
        listeners.remove(listener)
    }

    fun isConnected(): Boolean {
        val network = connectivityManager?.activeNetwork ?: return false
        val caps = connectivityManager?.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun destroy() {
        networkCallback?.let { connectivityManager?.unregisterNetworkCallback(it) }
        listeners.clear()
        lastKnownState = null
    }
}