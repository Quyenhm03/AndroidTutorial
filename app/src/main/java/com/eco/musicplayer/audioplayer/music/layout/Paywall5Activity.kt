package com.eco.musicplayer.audioplayer.music.layout

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityUnlockFeatureBinding

class Paywall5Activity : AppCompatActivity() {

    private lateinit var binding: ActivityUnlockFeatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnlockFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stateIsLoading()
        Handler(Looper.getMainLooper()).postDelayed({
            val extras = intent.extras
            val state = extras?.getInt("state", 0) ?: 0

            if (state == 0) {
                stateNormal()
            } else {
                stateNotEligible()
            }
        }, 2000)
    }

    @SuppressLint("SetTextI18n")
    private fun stateIsLoading() {
        binding.apply {
            btnTry.text = ""
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5F5F5F"))
            txtSubTitle.visibility = View.INVISIBLE
            progress.visibility = View.VISIBLE
            btnTry.isEnabled = false
        }
    }

    private fun stateNormal() {
        binding.apply {
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0E111B"))
            btnTry.text = getString(R.string.btn_try)
            btnTry.isEnabled = true

            txtSubTitle.visibility = View.VISIBLE
            txtSubTitle.text = getString(R.string.free_trial, "$14.99/year")
            progress.visibility = View.INVISIBLE
        }
    }

    private fun stateNotEligible() {
        binding.apply {
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0E111B"))
            btnTry.text = getString(R.string.btn_continue)
            btnTry.isEnabled = true

            txtSubTitle.visibility = View.VISIBLE
            txtSubTitle.text = getString(R.string.not_free_trial, "$14.99/year")
            progress.visibility = View.INVISIBLE
        }
    }
}