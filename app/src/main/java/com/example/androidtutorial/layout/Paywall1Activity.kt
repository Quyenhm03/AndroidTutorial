package com.example.androidtutorial.layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidtutorial.R
import com.example.androidtutorial.databinding.ActivityPwSale50Binding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class Paywall1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPwSale50Binding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwSale50Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomSheet()

        val extras = intent.extras
        val state = extras?.getInt("state", 0) ?: 0

        when(state) {
            0 -> stateIsLoading()
            1 -> stateLoaded()
            2 -> stateError()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.adLayout)

        val screenHeight = resources.displayMetrics.heightPixels
        val peekHeight = (screenHeight * 0.34).toInt()

        bottomSheetBehavior.apply {
            this.peekHeight = peekHeight
            isHideable = false
            isFitToContents = true
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    @SuppressLint("SetTextI18n")
    private fun stateIsLoading() {
        binding.groupPrice.visibility = View.INVISIBLE
        binding.groupError.visibility = View.GONE
        binding.groupLoading.visibility = View.VISIBLE
        binding.btnClaimOffer.text = ""
    }

    private fun stateLoaded() {
        binding.groupPrice.visibility = View.VISIBLE
        binding.groupError.visibility = View.GONE
        binding.groupLoading.visibility = View.GONE
        binding.btnClaimOffer.text = getString(R.string.btn_claim_offer)
    }

    private fun stateError() {
        binding.groupPrice.visibility = View.INVISIBLE
        binding.groupLoading.visibility = View.GONE
        binding.groupError.visibility = View.VISIBLE
        binding.btnClaimOffer.visibility = View.INVISIBLE
    }
}