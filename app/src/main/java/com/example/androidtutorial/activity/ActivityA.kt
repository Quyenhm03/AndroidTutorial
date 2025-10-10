package com.example.androidtutorial.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidtutorial.databinding.ActivityABinding


class ActivityA : AppCompatActivity() {
    private val TAG = "ActivityA"
    private val COUNT_KEY = "count_key"

    private lateinit var binding: ActivityABinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var cnt = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityABinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "OnCreate")

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    val result = data?.getStringExtra("result") ?: "no result"
                    binding.txtResult.apply {
                        visibility = VISIBLE
                        text = "Result from ActivityB: $result"
                    }
                }
            }

        setOnClick()

        returnState(savedInstanceState)
    }

    fun count() {
        cnt++
        binding.txtCount.text = cnt.toString()
    }

    fun reset() {
        cnt = 0
        binding.txtCount.text = cnt.toString()
    }

    // explicit intent
    fun switch() {
        val intent = Intent(this, ActivityB::class.java)
        val bundle = Bundle()
        bundle.putInt(COUNT_KEY, cnt)
        bundle.putString("count_str", "Receive from ActivityA")

        intent.putExtras(bundle)
        startActivity(intent)
    }

    // implicit intent
    @SuppressLint("QueryPermissionsNeeded")
    fun openWeb(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            Log.d(TAG, "Web is opened with URL: $url")
        } else {
            Toast.makeText(this, "Don't find web", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Don't find web")
        }
    }

    fun sendSMS(phoneNumber: String, message: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(this, "Message is sent", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "SMS sent to $phoneNumber")
            } catch (e: Exception) {
                Toast.makeText(this, "Send message failed", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error: ${e.message}")
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS("0123456789", "Hello, this is a test message!")
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Permission denied")
            }
        }
    }

    fun setOnClick() {
        binding.btnCount.setOnClickListener { count() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSwitch.setOnClickListener { switch() }
        binding.btnOpenWeb.setOnClickListener {
            openWeb("https://www.google.com/")
        }
        binding.btnSendMsg.setOnClickListener {
            sendSMS("0123456789", "Hello, this is a test message!")
        }
        binding.btnGetResult.setOnClickListener {
            val intent = Intent(this, ActivityB::class.java)
            launcher.launch(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COUNT_KEY, cnt)
    }

    fun returnState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            cnt = it.getInt(COUNT_KEY)
            binding.txtCount.text = cnt.toString()
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