package com.example.androidtutorial.layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.databinding.ActivityResultPwWeeklyBinding
import com.example.androidtutorial.databinding.ActivityResultPwYearlyBinding

class Paywall3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityResultPwYearlyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultPwYearlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        val state = extras?.getInt("state", 0) ?: 0

        when(state) {
            0 -> stateIsLoading()
            1 -> stateLoaded()
            2 -> stateError()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun stateIsLoading() {
        binding.apply {
            btnClaimOffer.text = ""
            progress.visibility = View.VISIBLE
            txtTry.visibility = View.GONE
        }
    }

    private fun stateLoaded() {
        binding.progress.visibility = View.INVISIBLE
    }

    private fun stateError() {
        binding.apply {
            btnClaimOffer.visibility = View.INVISIBLE
            progress.visibility = View.INVISIBLE
            llError.visibility = View.VISIBLE
            txtTry.visibility = View.GONE
        }
    }
}