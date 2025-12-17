package com.eco.musicplayer.audioplayer.music.ads.appopenads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.eco.musicplayer.audioplayer.music.ads.AdsManager
import com.eco.musicplayer.audioplayer.music.ads.SplashActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppOpenManager(private val myApplication: MyApplication) : Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    private var currentActivity: Activity? = null

    private var loadTime: Long = 0L

    private var activityCount: Int = 0
    private var isAppInForeground = false
    private var isFirstLaunch = true

    private val appOpenAdUnitId = "ca-app-pub-3940256099942544/9257395921"

    private val handler = Handler(Looper.getMainLooper())

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
                    loadTime = System.currentTimeMillis()
                    Log.d("AppOpenManager", "Ad Loaded at ${Date(loadTime)}")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("AppOpenManager", "Failed to load: ${error.message}")
                    appOpenAd = null
                }
            }
        )
    }

    private fun wasLoadTimeLessThanNHoursAgo(hours: Long): Boolean {
        val timeSinceLoad = System.currentTimeMillis() - loadTime
        val millisPerHour = 3600000L
        return timeSinceLoad < millisPerHour * hours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun showAdIfAvailable() {
        if (currentActivity is SplashActivity) {
            Log.d("AppOpenManager", "Skip showing ad on SplashActivity")
            return
        }

        if (isShowingAd) {
            Log.d("AppOpenManager", "Ad showing")
            return
        }

        if (!AdsManager.canShowFullScreenAd(myApplication)) {
            val remainingSeconds = AdsManager.getRemainingCoolOffSeconds(myApplication)
            Log.d("AppOpenManager", "Skip Open Ad. Remaining $remainingSeconds s coolOff")
            return
        }

        if (!isAdAvailable()) {
            Log.d("AppOpenManager", "Ad not ready, loading again")
            loadAd()
            return
        }

        if (currentActivity == null) {
            Log.d("AppOpenManager", "No current Activity")
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d("AppOpenManager", "Ad Showed")

                AdsManager.recordFullScreenAdShown(myApplication)
            }

            override fun onAdDismissedFullScreenContent() {
                isShowingAd = false
                appOpenAd = null
                Log.d("AppOpenManager", "Ad Dismissed")

                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.d("AppOpenManager", "Failed to show: ${error.message}")
                isShowingAd = false
                appOpenAd = null
                loadAd()
            }

            override fun onAdClicked() {
                Log.d("AppOpenManager", "Ad Clicked")
            }

            override fun onAdImpression() {
                Log.d("AppOpenManager", "Ad Impression")
            }
        }

        currentActivity?.let {
            Log.d("AppOpenManager", "Showing App Open Ad on ${it.javaClass.simpleName}")
            appOpenAd?.show(it)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        activityCount++

        Log.d("AppOpenManager", "onActivityStarted: ${activity.javaClass.simpleName}, activityCount = $activityCount, wasInBackground = ${!isAppInForeground}")

        currentActivity = activity

        if (!isAppInForeground) {
            isAppInForeground = true
            Log.d("AppOpenManager", "App moved to FOREGROUND")
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity

        Log.d("AppOpenManager", "onActivityResumed: ${activity.javaClass.simpleName}, activityCount = $activityCount, isFirstLaunch = $isFirstLaunch")

        if (!isFirstLaunch && activityCount == 1 && isAppInForeground && !isShowingAd) {
            Log.d("AppOpenManager", "App resumed from background - Attempting to show App Open Ad")

            handler.postDelayed({
                showAdIfAvailable()
            }, 100)
        }

        if (isFirstLaunch && activity !is SplashActivity) {
            isFirstLaunch = false
            Log.d("AppOpenManager", "First launch completed")
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--

        Log.d("AppOpenManager", "onActivityStopped: ${activity.javaClass.simpleName}, activityCount = $activityCount")

        if (activityCount == 0) {
            isAppInForeground = false
            Log.d("AppOpenManager", "App moved to BACKGROUND")
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}