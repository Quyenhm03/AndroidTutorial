package com.eco.musicplayer.audioplayer.music.layout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityItineraryBinding

class ItineraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItineraryBinding

    private var isDown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDetail1.setOnClickListener {
            proccessClickBtnDetail()
        }
    }

    fun proccessClickBtnDetail() {
        if (!isDown) {
            isDown = true
            binding.btnDetail1.setBackgroundResource(R.drawable.ic_down)
            binding.dayLayout.visibility = View.VISIBLE
        } else {
            isDown = false
            binding.btnDetail1.setBackgroundResource(R.drawable.ic_up)
            binding.dayLayout.visibility = View.GONE
        }
    }
}