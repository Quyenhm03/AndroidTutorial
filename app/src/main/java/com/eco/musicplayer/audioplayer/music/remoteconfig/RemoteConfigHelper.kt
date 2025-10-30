package com.eco.musicplayer.audioplayer.music.remoteconfig

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig

object RemoteConfigHelper {

    fun getString(key: String, default: String = ""): String {
        return Firebase.remoteConfig.getString(key).ifEmpty { default }
    }

    fun getInt(key: String, default: Int = 0): Int {
        val value = Firebase.remoteConfig.getLong(key).toInt()
        return value.takeIf { it != 0 } ?: default
    }

    fun getLong(key: String): Long {
        return Firebase.remoteConfig.getLong(key)
    }

    fun getBoolean(key: String): Boolean {
        return Firebase.remoteConfig.getBoolean(key)
    }
}