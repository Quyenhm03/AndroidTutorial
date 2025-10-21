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

        val state = 0
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
        val priceViews = listOf(binding.txtPriceStroke, binding.txtOff, binding.txtPrice, binding.txtPerWeek)
        priceViews.forEach { it.visibility = View.INVISIBLE }
        binding.viewLoad.visibility = View.VISIBLE
        binding.progress.visibility = View.VISIBLE
        binding.btnClaimOffer.text = ""
    }

    private fun stateLoaded() {
        val priceViews = listOf(binding.txtPriceStroke, binding.txtOff, binding.txtPrice, binding.txtPerWeek)
        priceViews.forEach { it.visibility = View.VISIBLE }
        binding.viewLoad.visibility = View.INVISIBLE
        binding.progress.visibility = View.INVISIBLE
        binding.btnClaimOffer.text = getString(R.string.btn_claim_offer)
    }

    private fun stateError() {
        val priceViews = listOf(binding.txtPriceStroke, binding.txtOff, binding.txtPrice,
            binding.txtPerWeek, binding.btnClaimOffer, binding.progress)
        priceViews.forEach { it.visibility = View.INVISIBLE }
        binding.llError.visibility = View.VISIBLE
    }
}