package com.eco.musicplayer.audioplayer.music.activityandservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.musicplayer.audioplayer.music.databinding.ActivityDemoConnectServiceBinding
import com.eco.musicplayer.audioplayer.music.permission.Permission

class ConnectServiceActivity : AppCompatActivity() {
    private var learnService: LearnService? = null
    private var isBound = false
    private lateinit var binding: ActivityDemoConnectServiceBinding
    private val TAG = "ConnectServiceActivity"

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LearnService.MyBinder
            learnService = binder.getService()
            isBound = true
            Log.d(TAG, "Service bound successfully")
            Toast.makeText(this@ConnectServiceActivity, learnService?.sayHello(), Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            Log.d(TAG, "Service disconnected")
        }
    }

    private lateinit var receiver: MessageBroadcastReceiver
    private val permission: Permission by lazy { Permission(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
        binding = ActivityDemoConnectServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiver = MessageBroadcastReceiver { msg ->
            Log.d(TAG, "Received: $msg")
            binding.txtReceiveFromBroadcast.visibility = View.VISIBLE
            binding.txtReceiveFromBroadcast.text = "Received: $msg"
        }

        registerReceiver(receiver, IntentFilter("com.eco.musicplayer.audioplayer.music.activityandservice.MESSAGE"))
        Log.d(TAG, "BroadcastReceiver registered")

        solveOnClick()
    }

    fun solveOnClick() {
        binding.btnStartService.setOnClickListener {
            Log.d(TAG, "Start Service clicked")
            val intent = Intent(this, LearnService::class.java)
            startService(intent)
        }

        binding.btnStopService.setOnClickListener {
            Log.d(TAG, "Stop Service clicked")
            val intent = Intent(this, LearnService::class.java)
            stopService(intent)
        }

        binding.btnBindService.setOnClickListener {
            Log.d(TAG, "Bind Service clicked")
            val intent = Intent(this, LearnService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        binding.btnUnbindService.setOnClickListener {
            Log.d(TAG, "Unbind Service clicked")
            if (isBound) {
                unbindService(connection)
                isBound = false
                Log.d(TAG, "Service unbound")
            } else {
                Log.w(TAG, "Cannot unbind: not bound")
            }
        }

        binding.btnStartIntentService.setOnClickListener {
            Log.d(TAG, "Start IntentService clicked")
            val intent = Intent(this, LearnIntentService::class.java).apply {
                putExtra("data", "Hello from Activity to IntentService")
            }
            startService(intent)
        }

        binding.btnPlayMusic.setOnClickListener {
            Log.d(TAG, "Play Music clicked → Requesting permission")
            permission.requestNotificationPermissionWithRetry(this) {
                Log.d(TAG, "Permission granted → Starting music")
                startMusicService()
            }
        }

        binding.btnPauseMusic.setOnClickListener {
            Log.d(TAG, "Pause Music clicked")
            controlMusic(MusicForegroundService.ACTION_PAUSE)
        }

        binding.btnStopMusic.setOnClickListener {
            Log.d(TAG, "Stop Music clicked")
            controlMusic(MusicForegroundService.ACTION_STOP)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: requestCode=$requestCode")

        permission.handlePermissionResult(requestCode, permissions, grantResults) {
            Log.d(TAG, "Permission granted in callback → startMusicService()")
            startMusicService()
        }
    }

    private fun startMusicService() {
        val intent = Intent(this, MusicForegroundService::class.java).apply {
            action = MusicForegroundService.ACTION_PLAY
            putExtra(MusicForegroundService.EXTRA_SONG_TITLE, "Count on me")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun controlMusic(action: String) {
        Log.d(TAG, "controlMusic: action=$action")
        startService(Intent(this, MusicForegroundService::class.java).apply {
            this.action = action
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
        unregisterReceiver(receiver)
        if (isBound) {
            unbindService(connection)
            Log.d(TAG, "Service unbound in onDestroy")
        }
    }
}