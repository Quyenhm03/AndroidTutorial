package com.eco.musicplayer.audioplayer.music.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission(private val context: Context) {

    companion object {
        const val REQUEST_CODE_PHOTO = 101
        const val REQUEST_CODE_STORAGE = 102
        const val REQUEST_CODE_AUDIO = 103
        const val REQUEST_CODE_VIDEO = 104
        const val REQUEST_CODE_NOTIFICATION = 105
        const val REQUEST_CODE_SEND_SMS = 106

        private const val PREF_NAME = "permission_prefs"
        private const val KEY_PREFIX = "deny_count_"
        private const val MAX_DENY_COUNT = 3
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun incrementDenyCount(permission: String) {
        val key = "$KEY_PREFIX$permission"
        val count = prefs.getInt(key, 0) + 1
        prefs.edit().putInt(key, count).apply()
    }

    private fun getDenyCount(permission: String): Int {
        val key = "$KEY_PREFIX$permission"
        return prefs.getInt(key, 0)
    }

    private fun resetDenyCount(permission: String) {
        val key = "$KEY_PREFIX$permission"
        prefs.edit().remove(key).apply()
    }

    fun requestPhotoPermissionWithRetry(activity: Activity) {
        requestPermissionWithRetry(
            activity = activity,
            permissions = getPhotoPermissions(),
            requestCode = REQUEST_CODE_PHOTO,
            permissionName = "Photo",
            onDeniedThree = { showGoToSettingsDialog(activity, "Photo") }
        )
    }

    fun requestStoragePermissionWithRetry(activity: Activity) {
        requestPermissionWithRetry(
            activity = activity,
            permissions = getStoragePermissions(),
            requestCode = REQUEST_CODE_STORAGE,
            permissionName = "Storage",
            onDeniedThree = { showGoToSettingsDialog(activity, "Storage") }
        )
    }

    fun requestAudioPermissionWithRetry(activity: Activity) {
        requestPermissionWithRetry(
            activity = activity,
            permissions = getAudioPermissions(),
            requestCode = REQUEST_CODE_AUDIO,
            permissionName = "Audio",
            onDeniedThree = { showGoToSettingsDialog(activity, "Audio") }
        )
    }

    fun requestVideoPermissionWithRetry(activity: Activity) {
        requestPermissionWithRetry(
            activity = activity,
            permissions = getVideoPermissions(),
            requestCode = REQUEST_CODE_VIDEO,
            permissionName = "Video",
            onDeniedThree = { showGoToSettingsDialog(activity, "Video") }
        )
    }

    fun requestNotificationPermissionWithRetry(activity: Activity) {
        requestPermissionWithRetry(
            activity = activity,
            permissions = getNotificationPermissions(),
            requestCode = REQUEST_CODE_NOTIFICATION,
            permissionName = "Notification",
            onDeniedThree = { showGoToSettingsDialog(activity, "Notification") }
        )
    }

    fun requestSendSMSPermissionWithRetry(activity: Activity) {
        requestPermissionWithRetry(
            activity = activity,
            permissions = arrayOf(Manifest.permission.SEND_SMS),
            requestCode = REQUEST_CODE_SEND_SMS,
            permissionName = "Send_sms",
            onDeniedThree = { showGoToSettingsDialog(activity, "Send_sms") }
        )
    }

    private fun requestPermissionWithRetry(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int,
        permissionName: String,
        onDeniedThree: () -> Unit
    ) {
        val mainPermission = permissions.firstOrNull() ?: return

        when {
            isPermissionGranted(permissions) -> {
                resetDenyCount(mainPermission)
            }
            else -> {
                val denyCount = getDenyCount(mainPermission)
                if (denyCount >= MAX_DENY_COUNT - 1) {
                    onDeniedThree()
                    resetDenyCount(mainPermission)
                } else {
                    ActivityCompat.requestPermissions(activity, permissions, requestCode)
                }
            }
        }
    }

    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onGranted: () -> Unit
    ) {
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            permissions.forEach { resetDenyCount(it) }
            onGranted()
        } else {
            permissions.forEach { incrementDenyCount(it) }
        }
    }

    private fun showGoToSettingsDialog(activity: Activity, permissionName: String) {
        AlertDialog.Builder(activity)
            .setTitle("Granted $permissionName")
            .setMessage("App is granted $permissionName. Please granted in Setting.")
            .setPositiveButton("Open Setting") { _, _ ->
                openAppSettings(activity)
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    private fun isPermissionGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getPhotoPermissions(): Array<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun getStoragePermissions(): Array<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
        else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun getAudioPermissions(): Array<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun getVideoPermissions(): Array<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
        else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun getNotificationPermissions(): Array<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        else -> emptyArray()
    }

}