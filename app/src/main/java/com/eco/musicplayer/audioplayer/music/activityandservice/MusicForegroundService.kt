package com.eco.musicplayer.audioplayer.music.activityandservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.eco.musicplayer.audioplayer.music.R

class MusicForegroundService : Service() {
    private val TAG = "MusicForegroundService"
    private var mediaPlayer: MediaPlayer? = null
    private val NOTIFICATION_ID = 101
    private val CHANNEL_ID = "music_player_channel"
    private var currentTitle: String = "Unknown"
    private var isPlaying: Boolean = false

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_SONG_TITLE = "song_title"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d(TAG, "onStartCommand() - action: $action")

        when (action) {
            ACTION_PLAY -> {
                currentTitle = intent?.getStringExtra(EXTRA_SONG_TITLE) ?: "Demo music"
                startForegroundWithNotification(currentTitle)
                playLocalMusic()
            }
            ACTION_PAUSE -> pauseMusic()
            ACTION_STOP -> stopAndCleanup()
        }
        return START_NOT_STICKY
    }

    private fun playLocalMusic() {
        Log.d(TAG, "playLocalMusic() → res/raw/viet_nam_trong_toi_la_1")
        try {
            mediaPlayer?.release()
            val uri = Uri.parse("android.resource://${packageName}/${R.raw.count_on_me}")
            mediaPlayer = MediaPlayer.create(this, uri).apply {
                setOnCompletionListener {
                    Log.d(TAG, "Music completed")
                    broadcast("Finished")
                    stopAndCleanup()
                }
                start()
            }
            isPlaying = true
            updateNotification(currentTitle, true)
            broadcast("Playing: $currentTitle")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing local music", e)
            broadcast("Play failed: ${e.message}")
            stopAndCleanup()
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPlaying = false
            updateNotification(currentTitle, false)
            broadcast("Paused")
        }
    }

    private fun stopAndCleanup() {
        Log.d(TAG, "stopAndCleanup() called")
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up", e)
        }
        isPlaying = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        broadcast("Stopped")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Music playback controls" }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, isPlaying: Boolean): Notification {
        val playPauseIntent = Intent(this, MusicForegroundService::class.java).apply {
            action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
            putExtra(EXTRA_SONG_TITLE, currentTitle)
        }
        val stopIntent = Intent(this, MusicForegroundService::class.java).apply {
            action = ACTION_STOP
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

        val playPausePending = PendingIntent.getService(this, 0, playPauseIntent, flags)
        val stopPending = PendingIntent.getService(this, 1, stopIntent, flags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Now Playing")
            .setContentText(title)
            .setSmallIcon(R.drawable.ic_music)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Play",
                playPausePending
            )
            .addAction(R.drawable.ic_stop, "Stop", stopPending)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(title: String, isPlaying: Boolean) {
        getSystemService(NotificationManager::class.java)?.notify(
            NOTIFICATION_ID, buildNotification(title, isPlaying)
        )
    }

    private fun broadcast(msg: String) {
        sendBroadcast(Intent("com.eco.musicplayer.audioplayer.music.activityandservice.MESSAGE").apply {
            putExtra("msg", msg)
        })
    }

    private fun startForegroundWithNotification(title: String) {
        startForeground(NOTIFICATION_ID, buildNotification(title, true))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopAndCleanup()
        super.onDestroy()
    }
}