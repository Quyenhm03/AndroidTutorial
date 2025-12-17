package com.eco.musicplayer.audioplayer.music.ads

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivityRewardedInterstitialAdsBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class RewardedInterstitialAdsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRewardedInterstitialAdsBinding.inflate(layoutInflater)
    }

    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var rewardPoints: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        loadRewardedInterstitialAd()

        binding.btnContinue.setOnClickListener {
            showRewardedInterstitialOrContinue()
        }
    }

    private fun loadRewardedInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        RewardedInterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/5354046379",
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    Toast.makeText(
                        this@RewardedInterstitialAdsActivity,
                        "Rewarded Interstitial Loaded",
                        Toast.LENGTH_SHORT
                    ).show()

                    setFullScreenContentCallback()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedInterstitialAd = null
                    Toast.makeText(
                        this@RewardedInterstitialAdsActivity,
                        "Load Failed: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun setFullScreenContentCallback() {
        rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Toast.makeText(
                    this@RewardedInterstitialAdsActivity,
                    "Rewarded Interstitial Shown",
                    Toast.LENGTH_SHORT
                ).show()

                AdsManager.recordFullScreenAdShown(this@RewardedInterstitialAdsActivity)
            }

            override fun onAdDismissedFullScreenContent() {
                Toast.makeText(
                    this@RewardedInterstitialAdsActivity,
                    "Rewarded Interstitial Closed",
                    Toast.LENGTH_SHORT
                ).show()

                rewardedInterstitialAd = null
                continueAction()
                loadRewardedInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Toast.makeText(
                    this@RewardedInterstitialAdsActivity,
                    "Failed to show: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()

                rewardedInterstitialAd = null
                continueAction()
                loadRewardedInterstitialAd()
            }

            override fun onAdClicked() {
                Toast.makeText(
                    this@RewardedInterstitialAdsActivity,
                    "Rewarded Interstitial Clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }
        }
    }

    private fun showRewardedInterstitialOrContinue() {
        if (!AdsManager.canShowFullScreenAd(this)) {
            val remainingSeconds = AdsManager.getRemainingCoolOffSeconds(this)
            Toast.makeText(
                this,
                "Bỏ qua quảng cáo. Vui lòng chờ $remainingSeconds giây nữa",
                Toast.LENGTH_LONG
            ).show()

            continueAction()
            loadRewardedInterstitialAd()
            return
        }

        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd?.show(this) { rewardItem ->
                val amount = rewardItem.amount
                val type = rewardItem.type

                rewardPoints += amount
                binding.tvRewardPoints.text = "Điểm thưởng hiện có: $rewardPoints"

                Toast.makeText(
                    this,
                    "Nhận thưởng (Rewarded Interstitial): +$amount $type",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                "Rewarded Interstitial chưa sẵn sàng, tiếp tục không có quảng cáo",
                Toast.LENGTH_SHORT
            ).show()
            continueAction()
            loadRewardedInterstitialAd()
        }
    }

    private fun continueAction() {
        Toast.makeText(
            this,
            "Tiếp tục luồng xử lý chính của ứng dụng...",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        rewardedInterstitialAd = null
        super.onDestroy()
    }
}