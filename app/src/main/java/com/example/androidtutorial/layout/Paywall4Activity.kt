package com.example.androidtutorial.layout

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.R
import com.example.androidtutorial.databinding.ActivityPwOnboardingBinding

class Paywall4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityPwOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwOnboardingBinding.inflate(layoutInflater)
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
            progress.visibility = View.VISIBLE
            btnTry.isEnabled = false
            btnTry.text = ""
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5F5F5F"))
            txtFee.visibility = View.INVISIBLE
        }
    }

    private fun stateLoaded() {
        binding.apply {

        }
    }

    private fun stateNotEnoughTrial() {
        binding.apply {
            progress.visibility = View.INVISIBLE
            btnTry.text = "Continue"
            llFreeTrial.visibility = View.GONE
        }
    }

    private fun solveToggleFee(text: String) {
        binding.toggleGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btnYearly -> {
                    binding.txtFee.text = "$14.99/year\nAuto renew, Cancel anytime"
                }
                R.id.btnWeekly -> {
                    binding.txtFee.text = "$4.99/week\nAuto renew, Cancel anytime"
                }
            }
        }
    }
}