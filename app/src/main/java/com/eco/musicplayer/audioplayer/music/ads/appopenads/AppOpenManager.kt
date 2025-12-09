package com.eco.musicplayer.audioplayer.music.ads.appopenads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenManager(private val myApplication: MyApplication) : Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    private var currentActivity: Activity? = null

    private val appOpenAdUnitId = "ca-app-pub-3940256099942544/9257395921"

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        loadAd()
    }

    private fun loadAd() {
        if (appOpenAd != null) {
            return
        }

        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            myApplication,
            appOpenAdUnitId,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                   appOpenAd = ad
                    Log.d("AppOpenManager", "Ad Loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("AppOpenManager", "Failed to load: ${error.message}")
                    appOpenAd = null
                }
            }
        )
    }

    fun showAdIfAvailable() {
        if (isShowingAd) return
        if (appOpenAd == null) {
            loadAd()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d("AppOpenManager", "Ad Showed")
            }

            override fun onAdDismissedFullScreenContent() {
                isShowingAd = false
                appOpenAd = null
                loadAd()
                Log.d("AppOpenManager", "Ad Dismissed")
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.d("AppOpenManager", "Failed to show: ${error.message}")
                isShowingAd = false
                appOpenAd = null
                loadAd()
            }
        }

        currentActivity?.let {
            appOpenAd?.show(it)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        showAdIfAvailable()
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }
}