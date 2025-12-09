package com.eco.musicplayer.audioplayer.music.ads

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivityNativeAdsBinding
import com.eco.musicplayer.audioplayer.music.databinding.ItemNativeAdsBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class NativeAdsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityNativeAdsBinding.inflate(layoutInflater)
    }

    private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        loadNativeAd()
    }

    private fun loadNativeAd() {
        // native ads option
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .build()

        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad : NativeAd ->
                if (isDestroyed) {
                    ad.destroy()
                    return@forNativeAd
                }

                nativeAd?.destroy()
                nativeAd = ad

                val adBinding = ItemNativeAdsBinding.inflate(layoutInflater)
                displayNativeAdView(ad, adBinding)

                binding.nativeAdContainer.removeAllViews()
                binding.nativeAdContainer.addView(adBinding.root)

                Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_SHORT).show()
            }
            .withNativeAdOptions(adOptions)
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Toast.makeText(
                        this@NativeAdsActivity,
                        "Native Ad Failed: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun displayNativeAdView(nativeAd: NativeAd, adBinding: ItemNativeAdsBinding) {
        val adView = adBinding.nativeAdView

        // set ad
        adBinding.adHeadline.text = nativeAd.headline

        nativeAd.mediaContent?.let {
            adBinding.adMedia.setMediaContent(it)
        }

        if (nativeAd.body != null) {
            adBinding.adBody.text = nativeAd.body
            adBinding.adBody.visibility = View.VISIBLE
        } else adBinding.adBody.visibility = View.GONE

        if (nativeAd.callToAction != null) {
            adBinding.txtCta.text = nativeAd.callToAction
            adBinding.adCallToAction.visibility = View.VISIBLE
        } else adBinding.adCallToAction.visibility = View.GONE

        if (nativeAd.icon != null) {
            adBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
            adBinding.adAppIcon.visibility = View.VISIBLE
        } else adBinding.adAppIcon.visibility = View.GONE

        if (nativeAd.starRating != null) {
            adBinding.adStars.rating = nativeAd.starRating!!.toFloat()
            adBinding.adStars.visibility = View.VISIBLE
        } else adBinding.adStars.visibility = View.GONE

        if (nativeAd.store != null) {
            adBinding.adStore.text = nativeAd.store
            adBinding.adStore.visibility = View.VISIBLE
        } else adBinding.adStore.visibility = View.GONE

        if (nativeAd.price != null) {
            adBinding.adPrice.text = nativeAd.price
            adBinding.adPrice.visibility = View.VISIBLE
        } else adBinding.adPrice.visibility = View.GONE

        if (nativeAd.advertiser != null) {
            adBinding.adAdvertiser.text = nativeAd.advertiser
            adBinding.adAdvertiser.visibility = View.VISIBLE
        } else adBinding.adAdvertiser.visibility = View.GONE

        adView.setNativeAd(nativeAd)
    }


    override fun onDestroy() {
        nativeAd?.destroy()
        nativeAd = null
        super.onDestroy()
    }
}