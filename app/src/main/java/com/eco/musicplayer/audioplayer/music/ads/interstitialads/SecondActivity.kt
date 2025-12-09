package com.eco.musicplayer.audioplayer.music.ads.interstitialads

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eco.musicplayer.audioplayer.music.R

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tv = TextView(this).apply {
            text = "Bạn đã chuyển sang Màn hình B"
            textSize = 22f
            gravity = Gravity.CENTER
        }

        setContentView(tv)
    }
}