package com.example.androidtutorial.layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.R
import com.example.androidtutorial.databinding.ActivityResultPwWeeklyBinding
import com.example.androidtutorial.databinding.ActivityResultPwYearlyBinding
import kotlin.random.Random

class Paywall3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityResultPwYearlyBinding // Đổi binding cho layout của Paywall3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultPwYearlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            btnClaimOffer.visibility = View.VISIBLE
            txtTry.visibility = View.GONE
        }
    }

    private fun stateLoaded() {
        binding.apply {
            progress.visibility = View.GONE
            btnClaimOffer.text = getString(R.string.btn_claim_offer)
            btnClaimOffer.isEnabled = true
            btnClaimOffer.visibility = View.VISIBLE
            llError.visibility = View.GONE
            txtTry.visibility = View.VISIBLE
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