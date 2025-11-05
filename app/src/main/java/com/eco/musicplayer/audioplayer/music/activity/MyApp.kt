package com.eco.musicplayer.audioplayer.music.activity

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.eco.musicplayer.audioplayer.music.network.NetworkMonitor

class MyApp : Application() {
    val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(this)
            .create(SharedViewModel::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        NetworkMonitor.init(this)
    }
}