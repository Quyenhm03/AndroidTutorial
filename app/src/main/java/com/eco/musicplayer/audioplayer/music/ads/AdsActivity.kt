package com.eco.musicplayer.audioplayer.music.ads

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.ads.interstitialads.InterstitialAdsActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivityAdsBinding

class AdsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAdsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root)

        binding.btnDemoBannerAds.setOnClickListener { v ->
            val intent = Intent(this, BannerAdsActivity::class.java)
            startActivity(intent)
        }

        binding.btnDemoInterstitialAds.setOnClickListener { v ->
            val intent = Intent(this, InterstitialAdsActivity::class.java)
            startActivity(intent)
        }

        binding.btnDemoNativeAds.setOnClickListener { v ->
            val intent = Intent(this, NativeAdsActivity::class.java)
            startActivity(intent)
        }

        binding.btnDemoRewardedAds.setOnClickListener { v ->
            val intent = Intent(this, RewardedAdsActivity::class.java)
            startActivity(intent)
        }

        binding.btnDemoRewardedInterstitialAds.setOnClickListener { v ->
            val intent = Intent(this, RewardedInterstitialAdsActivity::class.java)
            startActivity(intent)
        }
    }
}