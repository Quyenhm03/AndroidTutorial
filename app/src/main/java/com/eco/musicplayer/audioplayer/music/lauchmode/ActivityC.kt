package com.eco.musicplayer.audioplayer.music.lauchmode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivityCBinding

class ActivityC : AppCompatActivity() {
    private val TAG = "ActivityC"

    companion object {
        private var totalInstanceCount = 0
    }

    private lateinit var binding: ActivityCBinding

    private var instanceCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalInstanceCount++
        instanceCount = totalInstanceCount
        updateInfo()

        binding.btnToD.setOnClickListener {
            startActivity(Intent(this, ActivityD::class.java))
        }
        binding.btnToE.setOnClickListener {
            startActivity(Intent(this, ActivityE::class.java))
        }
    }


    fun updateInfo() {
        binding.txtInfo.text = "ActivityC\nInstance Count: $instanceCount\nTaskID: $taskId"
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.d(TAG, "onNewIntent called")
//    }
}