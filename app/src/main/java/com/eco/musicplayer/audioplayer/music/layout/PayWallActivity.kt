package com.eco.musicplayer.audioplayer.music.layout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.eco.musicplayer.audioplayer.music.databinding.ActivityPwBinding
import com.eco.musicplayer.audioplayer.music.remoteconfig.RemoteConfig
import com.eco.musicplayer.audioplayer.music.remoteconfig.model.PaywallConfig
import com.google.gson.Gson

class PayWallActivity : BaseActivity() {

    private lateinit var binding: ActivityPwBinding
    private lateinit var remoteConfig: RemoteConfig
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREF_NAME = "PaywallPrefs"
        private const val KEY_FIRST_LAUNCH = "is_first_launch"
        private const val TAG = "PayWallActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
        Log.d(TAG, "Is first launch: $isFirstLaunch")

        remoteConfig = RemoteConfig()

        if (isFirstLaunch) {
            remoteConfig.fetchAndActivate {
                checkAndLaunchPaywallFirstTime()
            }
        } else {
            remoteConfig.fetchAndActivate {
                Log.d(TAG, "RemoteConfig fetched for manual navigation")
            }
        }

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.apply {
            btnPW1Success.setOnClickListener { showPayWall(com.eco.musicplayer.audioplayer.music.layout.Paywall1Activity::class.java, 0) }
            btnPW1Error.setOnClickListener { showPayWall(com.eco.musicplayer.audioplayer.music.layout.Paywall1Activity::class.java, 1) }
            btnPW2Success.setOnClickListener { showPayWall(Paywall2Activity::class.java, 0) }
            btnPW2Error.setOnClickListener { showPayWall(Paywall2Activity::class.java, 1) }
            btnPW3Success.setOnClickListener { showPayWall(Paywall3Activity::class.java, 0) }
            btnPW3Error.setOnClickListener { showPayWall(Paywall3Activity::class.java, 1) }
            btnPW4Normal.setOnClickListener { showPaywall4WithRemoteConfig(0) }
            btnPW4NotEligible.setOnClickListener { showPaywall4WithRemoteConfig(1) }
            btnPW5Normal.setOnClickListener { showPayWall(Paywall5Activity::class.java, 0) }
            btnPW5NotEligible.setOnClickListener { showPayWall(Paywall5Activity::class.java, 1) }
            btnPW6Normal.setOnClickListener { showPayWall(Paywall6Activity::class.java, 0) }
            btnPW6NotEligible.setOnClickListener { showPayWall(Paywall6Activity::class.java, 1) }
            btnPW7Normal.setOnClickListener { showPayWall(Paywall7Activity::class.java, 0) }
            btnPW7NotEligible.setOnClickListener { showPayWall(Paywall7Activity::class.java, 1) }
        }
    }

    private fun checkAndLaunchPaywallFirstTime() {
        val paywallConfig = remoteConfig.getPaywallConfig()
        Log.d(TAG, "PaywallConfig: $paywallConfig")

        val shouldLaunch = paywallConfig?.uiType == "4" ||
                paywallConfig?.uiType == "04" ||
                paywallConfig?.uiType?.toIntOrNull() == 4

        if (shouldLaunch) {
            sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
            showPaywall4WithConfig(paywallConfig, 0)
        } else {
            Log.w(TAG, "Not launching Paywall4 - uiType doesn't match")
        }
    }

    private fun showPaywall4WithRemoteConfig(state: Int) {
        val paywallConfig = remoteConfig.getPaywallConfig()
        showPaywall4WithConfig(paywallConfig, state)
    }

    private fun showPaywall4WithConfig(config: PaywallConfig?, state: Int) {
        val intent = Intent(this, Paywall4Activity::class.java).apply {
            putExtra("state", state)
            config?.let {
                putExtra("paywall_config", Gson().toJson(it))
            }
        }
        startActivity(intent)
    }

    private fun showPayWall(activityClass: Class<out Activity>, state: Int) {
        val intent = Intent(this, activityClass).apply { putExtra("state", state) }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteConfig.destroy()
        sharedPreferences.edit().clear().apply()
    }
}