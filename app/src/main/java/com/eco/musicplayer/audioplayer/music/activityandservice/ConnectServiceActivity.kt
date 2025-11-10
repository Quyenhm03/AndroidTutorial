package com.eco.musicplayer.audioplayer.music.activityandservice

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityDemoConnectServiceBinding
import org.checkerframework.checker.units.qual.A

class ConnectServiceActivity : AppCompatActivity() {

    private var learnService: LearnService? = null
    private var isBound = false

    private lateinit var binding: ActivityDemoConnectServiceBinding

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LearnService.MyBinder
            learnService = binder.getService()
            isBound = true
            Toast.makeText(this@ConnectServiceActivity, learnService?.sayHello(), Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val msg = intent?.getStringExtra("msg")
            Toast.makeText(this@ConnectServiceActivity, msg, Toast.LENGTH_LONG).show()
            Log.d("ConnectServiceActivity", "Broadcast received: $msg")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDemoConnectServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // register broadcast receiver
        registerReceiver(receiver, IntentFilter("com.eco.musicplayer.audioplayer.music.activityandservice.MESSAGE"))

        solveOnClick()
    }

    fun solveOnClick() {
        binding.btnStartService.setOnClickListener {
            val intent = Intent(this, LearnService::class.java)
            startService(intent)
        }

        binding.btnStopService.setOnClickListener {
            val intent = Intent(this, LearnService::class.java)
            stopService(intent)
        }

        binding.btnBindService.setOnClickListener {
            val intent = Intent(this, LearnService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        binding.btnUnbindService.setOnClickListener {
            if (isBound) {
                unbindService(connection)
                isBound = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        if (isBound) {
            unbindService(connection)
        }
    }
}