package com.example.androidtutorial.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.R
import com.example.androidtutorial.databinding.ActivityABinding
import com.example.androidtutorial.databinding.ActivityBBinding

class ActivityB : AppCompatActivity() {
    private val TAG = "ActivityB"
    private val COUNT_KEY = "count_key"

    private lateinit var binding: ActivityBBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "OnCreate")

        val extras = intent.extras
        val count = extras?.getInt(COUNT_KEY, 0) ?: 0
        val countStr = extras?.getString("count_str") ?: "No string received"

        binding.txtReceive.text = count.toString()

        onClick()
    }

    fun onClick() {
        binding.btnReturn.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            val intent = Intent(this, ActivityA::class.java)
            intent.putExtra("result", binding.edtInput.text.toString())

            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "OnStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "OnResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "OnPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "OnStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "OnRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OnDestroy")
    }
}