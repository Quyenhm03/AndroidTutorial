package com.example.androidtutorial.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.R

class ActivityA : AppCompatActivity() {
    private val TAG = "ActivityA"
    private val COUNT_KEY = "count_key"

    private lateinit var btnCount: Button
    private lateinit var btnReset: Button
    private lateinit var btnSwitch: Button

    private lateinit var txtCount: TextView

    private var cnt = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a)
        Log.d(TAG, "OnCreate")

        initView()
        setOnClick()

        returnState(savedInstanceState)
    }

    fun initView() {
        btnCount = findViewById(R.id.btn_count)
        btnReset = findViewById(R.id.btn_reset)
        btnSwitch = findViewById(R.id.btn_switch)

        txtCount = findViewById(R.id.txt_count)
    }

    fun count() {
        cnt++
        txtCount.text = cnt.toString()
    }

    fun reset() {
        cnt = 0
        txtCount.text = cnt.toString()
    }

    fun switch() {
        val intent = Intent(this, ActivityB::class.java)
        intent.putExtra(COUNT_KEY, cnt)
        startActivity(intent)
    }

    fun setOnClick() {
        btnCount.setOnClickListener { count() }
        btnReset.setOnClickListener { reset() }
        btnSwitch.setOnClickListener { switch() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COUNT_KEY, cnt)
    }

    fun returnState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            cnt = savedInstanceState.getInt(COUNT_KEY)
            txtCount.text = cnt.toString()
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

/**
 * A-> B
 * onPause (A) -> onCreate (B) -> onStart (B) -> onResume (B) -> onStop (A)
 */