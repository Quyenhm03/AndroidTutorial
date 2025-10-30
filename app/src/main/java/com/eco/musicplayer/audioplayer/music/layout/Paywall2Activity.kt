package com.eco.musicplayer.audioplayer.music.layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityResultPwWeeklyBinding
import kotlin.random.Random

class Paywall2Activity : BaseActivity() {

    private lateinit var binding: ActivityResultPwWeeklyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultPwWeeklyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        binding.btnClose.setOnClickListener { finish() }

        val extras = intent.extras
        val state = extras?.getInt("state", 0) ?: 0

        stateIsLoading()

        Handler(Looper.getMainLooper()).postDelayed({
            when (state) {
                0 -> stateLoaded()
                1 -> stateError()
                else -> stateLoaded()
            }
        }, 2000)
    }

    @SuppressLint("SetTextI18n")
    private fun stateIsLoading() {
        binding.apply {
            btnClaimOffer.text = ""
            btnClaimOffer.isEnabled = false
            progress.visibility = View.VISIBLE
            llError.visibility = View.GONE
            txtTry.visibility = View.GONE
            btnClaimOffer.visibility = View.VISIBLE
        }
    }

    private fun stateLoaded() {
        binding.apply {
            progress.visibility = View.GONE
            btnClaimOffer.text = getString(R.string.btn_claim_offer)
            btnClaimOffer.isEnabled = true
            btnClaimOffer.visibility = View.VISIBLE
            txtTry.visibility = View.VISIBLE
            llError.visibility = View.GONE
        }
    }

    private fun stateError() {
        binding.apply {
            progress.visibility = View.GONE
            btnClaimOffer.visibility = View.INVISIBLE
            llError.visibility = View.VISIBLE
            txtTry.visibility = View.GONE

            btnTryAgain.setOnClickListener {
                stateIsLoading()
                simulateAfterTryAgain()
            }
        }
    }

    private fun simulateAfterTryAgain() {
        Handler(Looper.getMainLooper()).postDelayed({
            val isSuccess = Random.nextBoolean()
            if (isSuccess) {
                stateLoaded()
            } else {
                stateError()
            }
        }, 2000)
    }
}