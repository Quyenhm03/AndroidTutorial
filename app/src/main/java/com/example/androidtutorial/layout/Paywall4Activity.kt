package com.example.androidtutorial.layout

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        binding.btnClose.setOnClickListener { finish() }

        stateIsLoading()

        Handler(Looper.getMainLooper()).postDelayed({
            val extras = intent.extras
            val state = extras?.getInt("state", 0) ?: 0

            if (state == 0) {
                stateNormal()
            } else {
                stateNotEligible()
            }
        }, 2000)
    }

    private fun stateIsLoading() {
        binding.apply {
            progress.visibility = View.VISIBLE
            btnTry.isEnabled = false
            btnTry.text = ""
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5F5F5F"))
            txtFee.visibility = View.INVISIBLE
            swToggle.isEnabled = false
            swToggle.isChecked = true
        }
    }

    private fun stateNormal() {
        binding.apply {
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0F1E47"))
            btnTry.text = getString(R.string.btn_try)
            progress.visibility = View.GONE
            txtFee.visibility = View.VISIBLE
            llFreeTrial.visibility = View.VISIBLE
            btnTry.isEnabled = true

            swToggle.setOnCheckedChangeListener(null)

            swToggle.isEnabled = true
            swToggle.isChecked = true

            updateTrialText(swToggle.isChecked)
            updateFee(swToggle.isChecked, getSelectedPlan())

            swToggle.setOnCheckedChangeListener { _, isChecked ->
                updateTrialText(isChecked)
                updateFee(isChecked, getSelectedPlan())
            }

            toggleGroup.setOnCheckedChangeListener { _, _ ->
                updateFee(swToggle.isChecked, getSelectedPlan())
            }
        }
    }

    private fun stateNotEligible() {
        binding.apply {
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0F1E47"))
            progress.visibility = View.GONE
            btnTry.isEnabled = true
            btnTry.text = getString(R.string.btn_continue)
            llFreeTrial.visibility = View.GONE
            txtFee.visibility = View.VISIBLE

            val plan = getSelectedPlan()
            val price = if (plan == "WEEKLY") "$4.99/week" else "$19.99/year"
            txtFee.text = getString(R.string.not_free_trial, price)

            toggleGroup.setOnCheckedChangeListener { _, _ ->
                val newPlan = getSelectedPlan()
                val newPrice = if (newPlan == "WEEKLY") "$4.99/week" else "$19.99/year"
                txtFee.text = getString(R.string.not_free_trial, newPrice)
            }
        }
    }

    private fun getSelectedPlan(): String {
        return when (binding.toggleGroup.checkedRadioButtonId) {
            R.id.btnWeekly -> "WEEKLY"
            R.id.btnYearly -> "YEARLY"
            else -> "YEARLY"
        }
    }

    private fun updateTrialText(isTrialOn: Boolean) {
        binding.txtFreeTrial.text = if (isTrialOn) {
            getString(R.string.free_trial_enabled)
        } else {
            getString(R.string.enable_free_trial)
        }
    }

    private fun updateFee(isTrialOn: Boolean, plan: String) {
        binding.apply {
            val price = if (plan == "WEEKLY") "$4.99/week" else "$19.99/year"
            txtFee.text = if (isTrialOn) {
                getString(R.string.free_trial, price)
            } else {
                getString(R.string.not_free_trial, price)
            }
        }
    }
}