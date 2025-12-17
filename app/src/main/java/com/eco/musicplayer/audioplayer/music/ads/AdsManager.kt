package com.eco.musicplayer.audioplayer.music.ads

import android.content.Context

object AdsManager {

    private const val PREF_NAME = "AdsManagerPrefs"
    private const val KEY_LAST_FULLSCREEN_AD_TIME = "last_full_screen_ad_time"

    private const val COOL_OFF_TIME_MILLIS = 30000L

    fun canShowFullScreenAd(context: Context) : Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastShownTime = prefs.getLong(KEY_LAST_FULLSCREEN_AD_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastShownTime) >= COOL_OFF_TIME_MILLIS
    }

    fun recordFullScreenAdShown(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_FULLSCREEN_AD_TIME, System.currentTimeMillis()).apply()
    }

    fun getRemainingCoolOffTime(context: Context) : Long {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastShownTime = prefs.getLong(KEY_LAST_FULLSCREEN_AD_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - lastShownTime
        return if (elapsed < COOL_OFF_TIME_MILLIS) {
            COOL_OFF_TIME_MILLIS - elapsed
        } else {
            0L
        }
    }

    fun getRemainingCoolOffSeconds(context: Context) : Long {
        return getRemainingCoolOffTime(context) / 1000
    }

    fun resetCoolOffTIme(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_LAST_FULLSCREEN_AD_TIME).apply()
    }
}