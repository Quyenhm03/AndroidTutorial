package com.eco.musicplayer.audioplayer.music.layout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.eco.musicplayer.audioplayer.music.databinding.ActivityPwBinding

class PayWallActivity : BaseActivity() {

    private lateinit var binding: ActivityPwBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
        setStatusBarIconsColor(false)

        binding.apply {
            btnPW1Success.setOnClickListener{ showPayWall(com.eco.musicplayer.audioplayer.music.layout.Paywall1Activity::class.java, 0)}
            btnPW1Error.setOnClickListener{ showPayWall(com.eco.musicplayer.audioplayer.music.layout.Paywall1Activity::class.java, 1)}
            btnPW2Success.setOnClickListener{ showPaywall2Dialog( 0)}
            btnPW2Error.setOnClickListener{ showPaywall2Dialog(1)}
            btnPW3Success.setOnClickListener{ showPaywall3Dialog(0) }
            btnPW3Error.setOnClickListener{ showPaywall3Dialog(1) }
            btnPW4Normal.setOnClickListener{ showPayWall(Paywall4ActivityNewBilling::class.java, 0)}
            btnPW4NotEligible.setOnClickListener{ showPayWall(Paywall4ActivityNewBilling::class.java, 1)}
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

    private fun showPaywall2Dialog(state: Int) {
        val dialog = Paywall2Dialog(this, state)
        dialog.show()
    }

    private fun showPaywall3Dialog(state: Int) {
        val dialog = Paywall3Dialog(this, state)
        dialog.show()
    }
}