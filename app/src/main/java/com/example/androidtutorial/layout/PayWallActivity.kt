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
            btnPW1Success.setOnClickListener{ showPayWall(Paywall1Activity::class.java, 0)}
            btnPW1Error.setOnClickListener{ showPayWall(Paywall1Activity::class.java, 1)}
            btnPW2Success.setOnClickListener{ showPayWall(Paywall2Activity::class.java, 0)}
            btnPW2Error.setOnClickListener{ showPayWall(Paywall2Activity::class.java, 1)}
            btnPW3Success.setOnClickListener{ showPayWall(Paywall3Activity::class.java, 0)}
            btnPW3Error.setOnClickListener{ showPayWall(Paywall3Activity::class.java, 1)}
            btnPW4Normal.setOnClickListener{ showPayWall(Paywall4Activity::class.java, 0)}
            btnPW4NotEligible.setOnClickListener{ showPayWall(Paywall4Activity::class.java, 1)}
            btnPW5Normal.setOnClickListener{ showPayWall(Paywall5Activity::class.java, 0)}
            btnPW5NotEligible.setOnClickListener{ showPayWall(Paywall5Activity::class.java, 1)}
            btnPW6Normal.setOnClickListener{ showPayWall(Paywall6Activity::class.java, 0)}
            btnPW6NotEligible.setOnClickListener{ showPayWall(Paywall6Activity::class.java, 1)}
            btnPW7Normal.setOnClickListener{ showPayWall(Paywall7Activity::class.java, 0)}
            btnPW7NotEligible.setOnClickListener{ showPayWall(Paywall7Activity::class.java, 1)}
        }
    }

    private fun showPayWall(activityClass: Class<out Activity>, state: Int) {
        val intent = Intent(this, activityClass)
        intent.putExtra("state", state)
        startActivity(intent)
    }
}