package com.example.androidtutorial.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.R

class ActivityB : AppCompatActivity() {
    private val TAG = "ActivityB"
    private val COUNT_KEY = "count_key"

    private lateinit var btnReturn : Button
    private lateinit var txtReceive: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
        Log.d(TAG, "OnCreate")

        val receive = intent.getIntExtra(COUNT_KEY, 0)

        initView()
        txtReceive.text = receive.toString()

        btnReturn.setOnClickListener {
            finish()
        }
    }

    fun initView() {
        btnReturn = findViewById(R.id.btn_return)
        txtReceive = findViewById(R.id.txt_receive)
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