package com.eco.musicplayer.audioplayer.music.ads.appopenads

import android.app.Application
import com.google.android.gms.ads.MobileAds

class MyApplication : Application() {

    lateinit var appOpenManager: AppOpenManager

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)
        appOpenManager = AppOpenManager(this)
    }
}