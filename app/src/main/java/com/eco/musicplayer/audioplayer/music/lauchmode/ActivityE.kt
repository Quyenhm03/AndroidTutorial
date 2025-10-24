package com.eco.musicplayer.audioplayer.music.lauchmode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivityEBinding


class ActivityE : AppCompatActivity() {
    private val TAG = "ActivityE"

    companion object {
        private var totalInstanceCount = 0
    }

    private lateinit var binding: ActivityEBinding

    private var instanceCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalInstanceCount++
        instanceCount = totalInstanceCount
        updateInfo()

        binding.btnToC.setOnClickListener {
            startActivity(Intent(this, ActivityC::class.java))
        }
        binding.btnToD.setOnClickListener {
            startActivity(Intent(this, ActivityD::class.java))
        }
    }

    fun updateInfo() {
        binding.txtInfo.text = "ActivityE\nInstance Count: $instanceCount\nTaskID: $taskId"
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called")
    }
}