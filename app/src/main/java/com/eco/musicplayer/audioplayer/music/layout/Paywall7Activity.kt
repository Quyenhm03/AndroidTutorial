package com.eco.musicplayer.audioplayer.music.layout

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityPwFullScreenBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class Paywall7Activity : BaseActivity() {

    private lateinit var binding: ActivityPwFullScreenBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var currentPlan = "YEARLY"
    private var isTrialEligible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwFullScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarIconsColor(false)
        hideSystemUI()
        setupWindowInsets()

        binding.btnClose.setOnClickListener { finish() }

        setupBottomSheet()

        stateIsLoading()

        Handler(Looper.getMainLooper()).postDelayed({
            val extras = intent.extras
            val state = extras?.getInt("state", 0) ?: 0

            isTrialEligible = (state == 0)
            if (isTrialEligible) {
                stateNormal()
            } else {
                stateNotEligible()
            }
        }, 2000)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                top = systemBars.top,
                bottom = 0
            )

            WindowInsetsCompat.CONSUMED
        }

        window.statusBarColor = Color.parseColor("#FFF6E7")
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.adLayout)

        val screenHeight = resources.displayMetrics.heightPixels
        val minHeight = (screenHeight * 0.72).toInt()

        bottomSheetBehavior.apply {
            isFitToContents = true
            isHideable = false
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.adLayout.post {
            val contentHeight = binding.adLayout.height

            if (contentHeight < minHeight) {
                val params = binding.adLayout.layoutParams
                params.height = minHeight
                binding.adLayout.layoutParams = params

                bottomSheetBehavior.isDraggable = false
            } else {
                bottomSheetBehavior.peekHeight = minHeight
                bottomSheetBehavior.isDraggable = true
            }
        }
    }

    private fun stateIsLoading() {
        binding.apply {
            groupProgress.visibility = View.VISIBLE
            groupLoading.visibility = View.INVISIBLE
            llYearlyFee.visibility = View.INVISIBLE
            llWeeklyFee.visibility = View.INVISIBLE

            btnTry.isEnabled = false
            btnTry.text = ""
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5F5F5F"))

            btnIap1.setOnClickListener {
                selectPlan("YEARLY", isTrial = true)
            }
            btnIap2.setOnClickListener {
                selectPlan("WEEKLY", isTrial = true)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                progress1.visibility = View.GONE
                progress2.visibility = View.GONE
                llWeeklyFee.visibility = View.VISIBLE
                llYearlyFee.visibility = View.VISIBLE
            }, 1000)
        }
    }

    private fun stateNormal() {
        binding.apply {
            progress.visibility = View.GONE
            btnTry.isEnabled = true
            btnTry.text = getString(R.string.btn_try)
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8147FF"))

            groupLoading.visibility = View.VISIBLE
            llYearlyFee.visibility = View.VISIBLE
            llWeeklyFee.visibility = View.VISIBLE

            selectPlan(currentPlan, isTrial = true)

            btnIap1.setOnClickListener { selectPlan("YEARLY", isTrial = true) }
            btnIap2.setOnClickListener { selectPlan("WEEKLY", isTrial = true) }
        }
    }

    private fun stateNotEligible() {
        binding.apply {
            groupLoading.visibility = View.VISIBLE
            groupProgress.visibility = View.GONE

            btnTry.isEnabled = true
            btnTry.text = getString(R.string.btn_continue)
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8147FF"))

            txt3DayTrial.text = getString(R.string.cancel_anytime)

            btnIap1.setOnClickListener { selectPlan("YEARLY", isTrial = false) }
            btnIap2.setOnClickListener { selectPlan("WEEKLY", isTrial = false) }

            selectPlan(currentPlan, isTrial = false)
        }
    }

    private fun selectPlan(plan: String, isTrial: Boolean) {
        currentPlan = plan
        binding.apply {
            if (plan == "YEARLY") {
                btnIap1.setBackgroundResource(R.drawable.bg_btn_pw_bottom_sheet_selected)
                txtSave92.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8147FF"))
                btnIap2.setBackgroundResource(R.drawable.bg_btn_bottom_sheet_unselected)
            } else {
                btnIap1.setBackgroundResource(R.drawable.bg_btn_bottom_sheet_unselected)
                txtSave92.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#908DAC"))
                btnIap2.setBackgroundResource(R.drawable.bg_btn_pw_bottom_sheet_selected)
            }
            updateIntroduceFee(isTrial, plan)
        }
    }

    private fun updateIntroduceFee(isTrial: Boolean, plan: String) {
        val price = if (plan == "YEARLY") "$19.99/year" else "$4.99/week"
        binding.txtIntroduceFee.text = if (isTrial) {
            getString(R.string.free_trial, price)
        } else {
            getString(R.string.auto_renew, price)
        }
    }
}