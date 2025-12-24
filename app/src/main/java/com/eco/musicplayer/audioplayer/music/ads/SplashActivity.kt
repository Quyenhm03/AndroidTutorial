package com.eco.musicplayer.audioplayer.music.ads

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivitySplashBinding
import com.eco.musicplayer.audioplayer.music.ads.appopenads.MyApplication
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val handler = Handler(Looper.getMainLooper())

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false

    private val appOpenAdUnitId = "ca-app-pub-3940256099942544/9257395921"
    private val MAX_LOAD_TIME = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        (application as MyApplication).appOpenManager.setInSplashScreen(true)

        loadAppOpenAd()

        handler.postDelayed({
            if (!isShowingAd && !isFinishing) {
                Log.d("SplashActivity", "Timeout - navigating without ad")
                navigateToMainScreen()
            }
        }, MAX_LOAD_TIME)
    }

    private fun loadAppOpenAd() {
        if (isLoadingAd || appOpenAd != null) {
            return
        }

        isLoadingAd = true
        Log.d("SplashActivity", "Loading App Open Ad...")

        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            this,
            appOpenAdUnitId,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d("SplashActivity", "App Open Ad loaded successfully")
                    appOpenAd = ad
                    isLoadingAd = false
                    showAppOpenAd()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("SplashActivity", "Failed to load ad: ${error.message}")
                    isLoadingAd = false
                    appOpenAd = null

                    navigateToMainScreen()
                }
            }
        )
    }

    private fun showAppOpenAd() {
        if (appOpenAd == null) {
            navigateToMainScreen()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d("SplashActivity", "App Open Ad showed")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d("SplashActivity", "App Open Ad dismissed")
                isShowingAd = false
                appOpenAd = null
                navigateToMainScreen()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.d("SplashActivity", "Failed to show ad: ${error.message}")
                isShowingAd = false
                appOpenAd = null
                navigateToMainScreen()
            }

            override fun onAdClicked() {
                Log.d("SplashActivity", "App Open Ad clicked")
            }

            override fun onAdImpression() {
                Log.d("SplashActivity", "App Open Ad impression")
            }
        }

        Log.d("SplashActivity", "Showing App Open Ad")
        appOpenAd?.show(this)
    }

    private fun navigateToMainScreen() {
        if (isFinishing) return
        handler.removeCallbacksAndMessages(null)

        (application as MyApplication).appOpenManager.setInSplashScreen(false)

        startActivity(Intent(this, AdsActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
