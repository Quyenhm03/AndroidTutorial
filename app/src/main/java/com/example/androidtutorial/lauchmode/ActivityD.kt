package com.example.androidtutorial.lauchmode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtutorial.R

class ActivityD : AppCompatActivity() {
    private val TAG = "ActivityD"

    companion object {
        private var totalInstanceCount = 0
    }

    private lateinit var btnToC: Button
    private lateinit var btnToD: Button
    private lateinit var btnToE: Button
    private lateinit var txtInfo: TextView

    private var instanceCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_d)

        initView()

        totalInstanceCount++
        instanceCount = totalInstanceCount
        updateInfo()

        btnToC.setOnClickListener {
            startActivity(Intent(this, ActivityC::class.java))
        }
        btnToD.setOnClickListener {
            startActivity(Intent(this, ActivityD::class.java))
        }
        btnToE.setOnClickListener {
            startActivity(Intent(this, ActivityE::class.java))
        }
    }

    fun initView() {
        btnToC = findViewById(R.id.btn_to_c)
        btnToD = findViewById(R.id.btn_to_d)
        btnToE = findViewById(R.id.btn_to_e)
        txtInfo = findViewById(R.id.txt_info)
    }

    fun updateInfo() {
        txtInfo.text = "ActivityD\nInstance Count: $instanceCount\nTaskID: $taskId"
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