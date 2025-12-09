package com.eco.musicplayer.audioplayer.music.ads

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivityBannerAdsBinding
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

class BannerAdsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityBannerAdsBinding.inflate(layoutInflater)
    }

    private var adViewAdaptive: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // init mobile Ads SDK
        MobileAds.initialize(this)

        loadStandardBanner()
        loadInlineBanner()
        setupAdaptiveBanner()
        loadCollapsibleBanner()
    }

    private fun loadStandardBanner() {
        val adRequest = AdRequest.Builder().build()

        binding.adViewStandard.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Toast.makeText(this@BannerAdsActivity,
                    "Standard Banner: Ad Loaded",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Toast.makeText(this@BannerAdsActivity,
                    "Standard Banner: Failed - ${adError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdImpression() {
                Toast.makeText(this@BannerAdsActivity,
                    "Standard Banner: Impression Recorded",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdClicked() {
                Toast.makeText(this@BannerAdsActivity,
                    "Standard Banner: Ad Clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdOpened() {
                Toast.makeText(this@BannerAdsActivity,
                    "Standard Banner: Ad Opened (Overlay Shown)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdClosed() {
                Toast.makeText(this@BannerAdsActivity,
                    "Standard Banner: Ad Closed (User Returned)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.adViewStandard.loadAd(adRequest)
    }

    private fun setupAdaptiveBanner() {
        binding.containerAdaptive.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.containerAdaptive.width > 0) {
                        binding.containerAdaptive.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        loadAdaptiveBanner()
                    }
                }

            }
        )
    }

    private fun loadAdaptiveBanner() {
        adViewAdaptive?.let {
            binding.containerAdaptive.removeView(it)
            it.destroy()
        }

        val newAdView = AdView(this)
        newAdView.adUnitId = "ca-app-pub-3940256099942544/9214589741"

        val adaptiveSize = getAdaptiveAdSize()
        newAdView.setAdSize(adaptiveSize)

        adViewAdaptive = newAdView

        binding.containerAdaptive.addView(newAdView)

        val adRequest = AdRequest.Builder().build()
        newAdView.loadAd(adRequest)
    }

    private fun loadInlineBanner() {
        val adResquest = AdRequest.Builder().build()
        binding.adViewInline.loadAd(adResquest)
    }

    private fun getAdaptiveAdSize(): AdSize {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density
        var adWidthPixels = binding.containerAdaptive.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

    private fun loadCollapsibleBanner() {
        val extras = Bundle()
        extras.putString("collapsible", "bottom")

        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .build()

        binding.adViewCollapsible.loadAd(adRequest)
    }
}