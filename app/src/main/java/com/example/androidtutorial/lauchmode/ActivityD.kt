package com.example.androidtutorial.lauchmode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.R
import com.example.androidtutorial.databinding.ActivityABinding
import com.example.androidtutorial.databinding.ActivityDBinding

class ActivityD : AppCompatActivity() {
    private val TAG = "ActivityD"

    companion object {
        private var totalInstanceCount = 0
    }

    private lateinit var binding: ActivityDBinding

    private var instanceCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDBinding.inflate(layoutInflater)
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
        binding.btnToE.setOnClickListener {
            startActivity(Intent(this, ActivityE::class.java))
        }
    }

    fun updateInfo() {
        binding.txtInfo.text = "ActivityD\nInstance Count: $instanceCount\nTaskID: $taskId"
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called")
        updateInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        totalInstanceCount--
    }
}