package com.example.androidtutorial.layout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.databinding.ActivityPwBinding

class PayWallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPwBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnPW1Loading.setOnClickListener { showPayWall(Paywall1Activity::class.java, 0) }
            btnPW1Loaded.setOnClickListener { showPayWall(Paywall1Activity::class.java, 1) }
            btnPW1Error.setOnClickListener { showPayWall(Paywall1Activity::class.java, 2) }
            btnPW2Loading.setOnClickListener { showPayWall(Paywall2Activity::class.java, 0) }
            btnPW2Loaded.setOnClickListener { showPayWall(Paywall2Activity::class.java, 1) }
            btnPW2Error.setOnClickListener { showPayWall(Paywall2Activity::class.java, 2) }
            btnPW3Loading.setOnClickListener { showPayWall(Paywall3Activity::class.java, 0) }
            btnPW3Loaded.setOnClickListener { showPayWall(Paywall3Activity::class.java, 1) }
            btnPW3Error.setOnClickListener { showPayWall(Paywall3Activity::class.java, 2) }
            btnPW4Loading.setOnClickListener { showPayWall(Paywall4Activity::class.java, 0) }
            btnPW4Loaded.setOnClickListener { showPayWall(Paywall4Activity::class.java, 1) }
            btnPW4NotTrial.setOnClickListener { showPayWall(Paywall4Activity::class.java, 2) }
            btnPW5Loading.setOnClickListener { showPayWall(Paywall5Activity::class.java, 0) }
            btnPW5Loaded.setOnClickListener { showPayWall(Paywall5Activity::class.java, 1) }
            btnPW5NotTrial.setOnClickListener { showPayWall(Paywall5Activity::class.java, 2) }
        }
    }

    private fun showPayWall(activityClass: Class<out Activity>, state: Int) {
        val intent = Intent(this, activityClass)
        intent.putExtra("state", state)
        startActivity(intent)
    }
}