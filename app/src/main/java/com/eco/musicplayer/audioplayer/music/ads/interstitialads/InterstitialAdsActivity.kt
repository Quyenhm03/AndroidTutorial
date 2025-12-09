package com.eco.musicplayer.audioplayer.music.ads.interstitialads

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        loadInterstitialAd()

        binding.btnNext.setOnClickListener {
            showInterstitialOrNavigate()
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
                    Toast.makeText(this@InterstitialAdsActivity, "Interstitial loaded", Toast.LENGTH_SHORT).show()

                    setupIntertitialCallbacks()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Toast.makeText(this@InterstitialAdsActivity,  "Load interstitial error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun setupIntertitialCallbacks() {
        interstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "Interstitial is showing",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdClicked() {
                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "User clicked ads",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdDismissedFullScreenContent() {
                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "Ad closed -> navigate",
                        Toast.LENGTH_SHORT
                    ).show()

                    interstitialAd = null
                    loadInterstitialAd()
                    navigateToNextScreen()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    Toast.makeText(
                        this@InterstitialAdsActivity,
                        "Cann't show ads",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToNextScreen()
                }
            }
    }

    private fun showInterstitialOrNavigate() {
        if (interstitialAd != null) {
            interstitialAd?.show(this)
        } else {
            Toast.makeText(this, "Ad not ready → navigate", Toast.LENGTH_SHORT).show()
            navigateToNextScreen()
        }
    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
}