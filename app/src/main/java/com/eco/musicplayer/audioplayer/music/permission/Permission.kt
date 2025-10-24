package com.eco.musicplayer.audioplayer.music.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission(private val context: Context) {

    fun isHasPermissionSendSMS() : Boolean {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED)
    }

    fun requestPermissionSendMSM(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), requestCode)
    }
}