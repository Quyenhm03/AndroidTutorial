package com.eco.musicplayer.audioplayer.music.ads.appopenads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
    private var wasInBackground = false

    private var isInSplashScreen = true

    private val appOpenAdUnitId = "ca-app-pub-3940256099942544/9257395921"

    private val handler = Handler(Looper.getMainLooper())

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        handler.postDelayed({
            loadAd()
        }, 2000)
    }

    fun setInSplashScreen(inSplash: Boolean) {
        isInSplashScreen = inSplash
        Log.d("AppOpenManager", "setInSplashScreen: $inSplash")
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
        if (isInSplashScreen) {
            Log.d("AppOpenManager", "Skip - Still in splash screen")
            return
        }

        if (currentActivity is SplashActivity) {
            Log.d("AppOpenManager", "Skip showing ad on SplashActivity")
            return
        }

        if (isShowingAd) {
            Log.d("AppOpenManager", "Ad is already showing")
            return
        }

        if (!isAppInForeground) {
            Log.d("AppOpenManager", "App is not in foreground, skip showing ad")
            return
        }

        if (!wasInBackground) {
            Log.d("AppOpenManager", "Not returning from background, skip")
            return
        }

        if (!isAdAvailable()) {
            Log.d("AppOpenManager", "Ad expired/not ready -> drop & reload")
            appOpenAd = null
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
                wasInBackground = false
                Log.d("AppOpenManager", "Ad Showed")
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
                wasInBackground = false
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
        currentActivity = activity

        val wasInBackgroundBefore = !isAppInForeground

        if (!isAppInForeground) {
            isAppInForeground = true
            if (activityCount == 1 && !isInSplashScreen) {
                wasInBackground = true
                Log.d("AppOpenManager", "App moved to FOREGROUND from BACKGROUND")
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity

        if (wasInBackground && !isInSplashScreen && !isShowingAd) {
            Log.d("AppOpenManager", "App resumed from background - showing ad")

            handler.postDelayed({
                showAdIfAvailable()
            }, 300)
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--

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