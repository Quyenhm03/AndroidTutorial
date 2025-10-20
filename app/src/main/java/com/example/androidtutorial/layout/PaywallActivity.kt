package com.example.androidtutorial.layout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PaywallActivity : AppCompatActivity() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pw_sale_50)

        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        val adLayout = findViewById<View>(R.id.adLayout)
        bottomSheetBehavior = BottomSheetBehavior.from(adLayout)

        val screenHeight = resources.displayMetrics.heightPixels
        val peekHeight = (screenHeight * 0.34).toInt()

        bottomSheetBehavior.apply {
            this.peekHeight = peekHeight
            isHideable = false
            isFitToContents = true
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}