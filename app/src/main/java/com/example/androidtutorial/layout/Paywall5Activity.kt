package com.example.androidtutorial.layout

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.databinding.ActivityResultPwYearlyBinding
import com.example.androidtutorial.databinding.ActivityUnlockFeatureBinding

class Paywall5Activity : AppCompatActivity() {
    private lateinit var binding: ActivityUnlockFeatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnlockFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        val state = extras?.getInt("state", 0) ?: 0

        when(state) {
            0 -> stateIsLoading()
            1 -> stateLoaded()
            2 -> stateNotEnoughTrial()
        }
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

    private fun stateLoaded() {
        binding.apply {
            progress.visibility = View.INVISIBLE
            btnTry.isEnabled = true
        }
    }

    private fun stateNotEnoughTrial() {
        binding.apply {
            btnTry.text = "Continue"
            txtSubTitle.text = "$14.99/year\nAuto renew, cancel anytime"
            progress.visibility = View.INVISIBLE
        }
    }
}