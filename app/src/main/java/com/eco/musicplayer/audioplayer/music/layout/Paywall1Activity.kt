package com.eco.musicplayer.audioplayer.music.layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityPwSale50Binding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.random.Random

class Paywall1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPwSale50Binding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwSale50Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomSheet()
        binding.btnClose.setOnClickListener { finish() }

        stateIsLoading()

        val extras = intent.extras
        val state = extras?.getInt("state", 0) ?: 0

        Handler(Looper.getMainLooper()).postDelayed({
            when (state) {
                0 -> stateLoaded()
                1 -> stateError()
            }
        }, 2000)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.adLayout)

        val screenHeight = resources.displayMetrics.heightPixels
        val peekHeight = (screenHeight * 0.4).toInt()

        bottomSheetBehavior.apply {
            this.peekHeight = peekHeight
            isHideable = false
            isFitToContents = true
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    @SuppressLint("SetTextI18n")
    private fun stateIsLoading() {
        binding.apply {
            groupPrice.visibility = View.INVISIBLE
            groupError.visibility = View.GONE
            groupLoading.visibility = View.VISIBLE
            btnClaimOffer.text = ""
            btnClaimOffer.isEnabled = false
            btnClaimOffer.visibility = View.VISIBLE
        }
    }

    private fun stateLoaded() {
        binding.apply {
            groupPrice.visibility = View.VISIBLE
            groupError.visibility = View.GONE
            groupLoading.visibility = View.GONE
            btnClaimOffer.text = getString(R.string.btn_claim_offer)
            btnClaimOffer.isEnabled = true
            btnClaimOffer.visibility = View.VISIBLE
        }
    }

    private fun stateError() {
        binding.apply {
            groupPrice.visibility = View.INVISIBLE
            groupLoading.visibility = View.GONE
            groupError.visibility = View.VISIBLE
            btnClaimOffer.visibility = View.INVISIBLE
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