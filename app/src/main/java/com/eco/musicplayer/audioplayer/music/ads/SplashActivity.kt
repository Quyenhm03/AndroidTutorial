package com.eco.musicplayer.audioplayer.music.ads

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivitySplashBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd

class SplashActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    private var isLoadingAd = false

    private val appOpenAdUnitId = "ca-app-pub-3940256099942544/9257395921"

    private val handler = Handler(Looper.getMainLooper())
    private val MAX_LOAD_TIMEOUT = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        updateLoadingState(true, "Đang khởi tạo...")

        MobileAds.initialize(this) {
            Log.d("SplashActivity", "MobileAds initialized")
            updateLoadingState(true, "Đang tải quảng cáo...")
            loadAppOpenAd()
        }

        handler.postDelayed({
            if (!isShowingAd && !isFinishing) {
                Log.d("SplashActivity", "Timeout: Navigate without ad")
                updateLoadingState(false, "Hết thời gian chờ...")
                navigateToMainScreen()
            }
        }, MAX_LOAD_TIMEOUT)
    }

    private fun updateLoadingState(isLoading: Boolean, message: String) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvLoading.text = message
        Log.d("SplashActivity", message)
    }

    private fun loadAppOpenAd() {
        if (isLoadingAd || appOpenAd != null) {
            return
        }

        isLoadingAd = true
        updateLoadingState(true, "Đang tải quảng cáo...")

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

                    updateLoadingState(false, "Quảng cáo đã sẵn sàng")

                    showAppOpenAd()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("SplashActivity", "Failed to load App Open Ad: ${error.message}")
                    isLoadingAd = false
                    appOpenAd = null

                    updateLoadingState(false, "Không thể tải quảng cáo")

                    Toast.makeText(
                        this@SplashActivity,
                        "Không thể tải quảng cáo: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    handler.postDelayed({
                        navigateToMainScreen()
                    }, 500)
                }
            }
        )
    }

    private fun showAppOpenAd() {
        if (isShowingAd) {
            Log.d("SplashActivity", "Ad is already showing")
            return
        }

        if (appOpenAd == null) {
            Log.d("SplashActivity", "App Open Ad is null")
            navigateToMainScreen()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d("SplashActivity", "App Open Ad showed")

                AdsManager.recordFullScreenAdShown(this@SplashActivity)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d("SplashActivity", "App Open Ad dismissed")
                isShowingAd = false
                appOpenAd = null

                navigateToMainScreen()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e("SplashActivity", "Failed to show App Open Ad: ${error.message}")
                isShowingAd = false
                appOpenAd = null

                Toast.makeText(
                    this@SplashActivity,
                    "Không thể hiển thị quảng cáo",
                    Toast.LENGTH_SHORT
                ).show()

                navigateToMainScreen()
            }

            override fun onAdClicked() {
                Log.d("SplashActivity", "App Open Ad clicked")
            }

            override fun onAdImpression() {
                Log.d("SplashActivity", "App Open Ad impression recorded")
            }
        }

        Log.d("SplashActivity", "Showing App Open Ad")
        appOpenAd?.show(this)
    }

    private fun navigateToMainScreen() {
        if (isFinishing) {
            return
        }

        Log.d("SplashActivity", "Navigating to AdsActivity")

        val intent = Intent(this, AdsActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        appOpenAd = null
        super.onDestroy()
    }
}