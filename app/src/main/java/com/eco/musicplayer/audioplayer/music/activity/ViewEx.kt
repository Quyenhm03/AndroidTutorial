package com.eco.musicplayer.audioplayer.music.activity

import android.view.View

private val lastClickTimeMap = mutableMapOf<View, Long>()

fun View.setSafeOnClickListener(interval: Long = 5000L, onSafeClick: (View) -> Unit) {
    setOnClickListener { view ->
        val currentTime = System.currentTimeMillis()
        val lastClickTime = lastClickTimeMap[view] ?: 0L

        if (currentTime - lastClickTime >= interval) {
            lastClickTimeMap[view] = currentTime
            onSafeClick(view)
        }
    }
}