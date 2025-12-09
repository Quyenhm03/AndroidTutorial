package com.eco.musicplayer.audioplayer.music.ads

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityRewardedAdsBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRewardedAdsBinding.inflate(layoutInflater)
    }

    private var rewardedAd: RewardedAd?= null
    private var rewardPoints: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        loadRewardedAd()

        binding.btnShowRewardedAd.setOnClickListener {
            showRewardedAd()
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            this,
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Toast.makeText(
                        this@RewardedAdsActivity,
                        "Rewarded Ad Loaded",
                        Toast.LENGTH_SHORT
                    ).show()

                    setFullScreenContentCallback()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    Toast.makeText(
                        this@RewardedAdsActivity,
                        "Load Rewarded Failed: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun setFullScreenContentCallback() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Toast.makeText(
                    this@RewardedAdsActivity,
                    "Rewarded Ad Showed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdDismissedFullScreenContent() {
                Toast.makeText(
                    this@RewardedAdsActivity,
                    "Rewarded Ad Closed",
                    Toast.LENGTH_SHORT
                ).show()

                rewardedAd = null
                loadRewardedAd() // load for next view
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Toast.makeText(
                    this@RewardedAdsActivity,
                    "Failed to show Rewarded: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                rewardedAd = null
                loadRewardedAd()
            }

            override fun onAdClicked() {
                Toast.makeText(
                    this@RewardedAdsActivity,
                    "Rewarded Ad Clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }
        }
    }

     private fun showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd?.show(
                this,
                OnUserEarnedRewardListener { rewardItem ->
                    val amount = rewardItem.amount
                    val type = rewardItem.type

                    rewardPoints += amount
                    binding.tvReward.text = "Điểm thưởng hiện có: $rewardPoints"

                    Toast.makeText(
                        this,
                        "Nhận thưởng: +$amount $type",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        } else {
            Toast.makeText(
                this,
                "Rewarded Ad chưa sẵn sàng, đang load lại...",
                Toast.LENGTH_SHORT
            ).show()
            loadRewardedAd()
        }
     }

    override fun onDestroy() {
        rewardedAd = null
        super.onDestroy()
    }
}