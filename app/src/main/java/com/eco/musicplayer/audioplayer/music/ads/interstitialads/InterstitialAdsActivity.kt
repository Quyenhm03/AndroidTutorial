package com.eco.musicplayer.audioplayer.music.ads.interstitialads

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.ads.AdsManager
import com.eco.musicplayer.audioplayer.music.databinding.ActivityInterstitialAdsBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityInterstitialAdsBinding.inflate(layoutInflater)
    }

    private var interstitialAd: InterstitialAd? = null

    private var isShowingAd = false
    private var adShowStartTime: Long = 0L
    private var shouldNavigateAfterAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        loadInterstitialAd()

        binding.btnNext.setOnClickListener {
            showInterstitialOrNavigate()
        }
    }

    override fun onResume() {
        super.onResume()

        if (isShowingAd) {
            val elapsed = System.currentTimeMillis() - adShowStartTime
            Toast.makeText(
                this,
                "Quảng cáo tiếp tục (đã xem ${elapsed / 1000}s)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "Interstitial loaded",
                        Toast.LENGTH_SHORT
                    ).show()

                    setupIntertitialCallbacks()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "Load interstitial error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun setupIntertitialCallbacks() {
        interstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                    adShowStartTime = System.currentTimeMillis()

                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "Interstitial is showing",
                        Toast.LENGTH_SHORT
                    ).show()

                    AdsManager.recordFullScreenAdShown(this@InterstitialAdsActivity)
                }

                override fun onAdClicked() {
                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "User clicked ads",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdDismissedFullScreenContent() {
                    isShowingAd = false

                    val duration = System.currentTimeMillis() - adShowStartTime
                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "Ad closed (đã xem ${duration / 1000}s) -> navigate",
                        Toast.LENGTH_SHORT
                    ).show()

                    interstitialAd = null
                    loadInterstitialAd()

                    if (shouldNavigateAfterAd) {
                        shouldNavigateAfterAd = false
                        navigateToNextScreen()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    isShowingAd = false

                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "Can't show ads",
                        Toast.LENGTH_SHORT
                    ).show()
                    interstitialAd = null
                    loadInterstitialAd()

                    if (shouldNavigateAfterAd) {
                        shouldNavigateAfterAd = false
                        navigateToNextScreen()
                    }
                }
            }
    }

    private fun showInterstitialOrNavigate() {
        if (isShowingAd) {
            Toast.makeText(
                this,
                "Quảng cáo đang hiển thị",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!AdsManager.canShowFullScreenAd(this)) {
            val remainingSeconds = AdsManager.getRemainingCoolOffSeconds(this)
            Toast.makeText(
                this,
                "Bỏ qua quảng cáo. Vui lòng chờ $remainingSeconds giây nữa",
                Toast.LENGTH_LONG
            ).show()

            navigateToNextScreen()
            return
        }

        if (interstitialAd != null) {
            shouldNavigateAfterAd = true
            interstitialAd?.show(this)
        } else {
            Toast.makeText(
                this,
                "Ad not ready → navigate",
                Toast.LENGTH_SHORT
            ).show()
            navigateToNextScreen()
        }
    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
}