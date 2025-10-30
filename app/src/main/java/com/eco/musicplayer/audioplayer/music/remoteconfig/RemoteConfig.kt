package com.eco.musicplayer.audioplayer.music.remoteconfig

import android.util.Log
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.remoteconfig.model.PaywallConfig
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import com.google.gson.Gson

class RemoteConfig {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        Firebase.remoteConfig.apply {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .setFetchTimeoutInSeconds(0)
                .build()
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
            reset()
        }
    }

    private var onComplete: (() -> Unit)? = null

    fun fetchAndActivate(complete: (() -> Unit)? = null) {
        this.onComplete = complete
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("RemoteConfig", "Fetch and activate successful")
                onComplete?.invoke()
            } else {
                Log.e("RemoteConfig", "Fetch and activate failed: ${it.exception?.message}")
            }
        }
    }

    fun getPaywallConfig(): PaywallConfig? {
        return try {
            val configJson = remoteConfig.getString("paywall_config")

            if (configJson.isNotEmpty()) {
                val config = Gson().fromJson(configJson, PaywallConfig::class.java)
                Log.d("RemoteConfig", "Parsed config: $config")
                config
            } else {
                Log.w("RemoteConfig", "Config JSON is empty")
                null
            }
        } catch (e: Exception) {
            Log.e("RemoteConfig", "Error parsing paywall config: ${e.message}", e)
            null
        }
    }

    fun registerRealtimeUpdate() {
        runCatching {
            remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    Log.d("RemoteConfig", "onUpdate")
                    remoteConfig.activate().addOnCompleteListener {
                        if (it.isSuccessful) {
                            onComplete?.invoke()
                        }
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    Log.e("RemoteConfig", "Config update error: ${error.message}")
                }
            })
        }.getOrElse {
            Log.e("RemoteConfig", "Error registering realtime update: ${it.message}")
        }
    }

    fun destroy() {
        onComplete = null
    }
}
