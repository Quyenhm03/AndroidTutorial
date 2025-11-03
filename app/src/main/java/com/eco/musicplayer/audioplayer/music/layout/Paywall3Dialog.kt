package com.eco.musicplayer.audioplayer.music.layout

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityResultPwYearlyBinding
import kotlin.random.Random

class Paywall3Dialog(context: Context, private val state: Int) : Dialog(context) {

    private lateinit var binding: ActivityResultPwYearlyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = ActivityResultPwYearlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpDialog()

        binding.btnClose.setOnClickListener { dismiss() }

        stateIsLoading()

        Handler(Looper.getMainLooper()).postDelayed({
            when (state) {
                0 -> stateLoaded()
                1 -> stateError()
                else -> stateLoaded()
            }
        }, 2000)
    }

    private fun setUpDialog() {
        // Cấu hình window để dialog full screen và trong suốt
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun stateIsLoading() {
        binding.apply {
            btnClaimOffer.text = ""
            btnClaimOffer.isEnabled = false
            txt30Percent.visibility = View.INVISIBLE
            progress.visibility = View.VISIBLE
            llError.visibility = View.GONE
            txtTry.visibility = View.GONE
            btnClaimOffer.visibility = View.VISIBLE
        }
    }

    private fun stateLoaded() {
        binding.apply {
            progress.visibility = View.GONE
            btnClaimOffer.text = context.getString(R.string.btn_claim_offer)
            btnClaimOffer.isEnabled = true
            btnClaimOffer.visibility = View.VISIBLE
            txt30Percent.visibility = View.VISIBLE
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
            txt30Percent.visibility = View.INVISIBLE

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
