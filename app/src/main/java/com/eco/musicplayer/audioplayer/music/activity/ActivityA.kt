package com.eco.musicplayer.audioplayer.music.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import com.eco.musicplayer.audioplayer.music.databinding.ActivityABinding
import com.eco.musicplayer.audioplayer.music.layout.BaseActivity
import com.eco.musicplayer.audioplayer.music.permission.Permission
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class ActivityA : BaseActivity() {
    private val TAG = "ActivityA"
    private val COUNT_KEY = "count_key"

    private lateinit var binding: ActivityABinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val permission: Permission by lazy { Permission(this) }

    private val viewModel: CounterViewModel by viewModels()

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

        solveViewModel()
        setOnClick()

        sendObject()

//        returnState(savedInstanceState)
    }

    fun solveViewModel() {
        viewModel.count.observe(this, Observer { value ->
            binding.txtCount.text = value.toString()
        })

        binding.btnIncrement.setOnClickListener {
            viewModel.increment()
        }

        binding.btnDecrement.setOnClickListener {
            viewModel.decrement()
        }
    }

    // explicit intent
    fun switch() {
        val intent = Intent(this, ActivityB::class.java)
        val bundle = Bundle()
        bundle.putString(COUNT_KEY, binding.txtCount.text.toString())
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
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "Message is sent", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "SMS sent to $phoneNumber")
        } catch (e: Exception) {
            Toast.makeText(this, "Send message failed: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error sending SMS: ${e.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Permission.REQUEST_CODE_SEND_SMS -> {
                permission.handlePermissionResult(
                    requestCode = requestCode,
                    permissions = permissions,
                    grantResults = grantResults,
                    onGranted = {
                        sendSMS("0123456789", "Hello, this is a test message!")
                    }
                )
            }
            Permission.REQUEST_CODE_PHOTO -> {
                permission.handlePermissionResult(
                    requestCode = requestCode,
                    permissions = permissions,
                    grantResults = grantResults,
                    onGranted = {
                        Toast.makeText(this, "Permission access photo is granted!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    fun setOnClick() {
        binding.btnSwitch.setOnClickListener { switch() }

        binding.btnOpenWeb.setOnClickListener {
            openWeb("https://www.google.com/")
        }

        binding.btnSendMsg.setSafeOnClickListener {
            permission.requestSendSMSPermissionWithRetry(this)
        }

        binding.btnGetResult.setOnClickListener {
            val intent = Intent(this, ActivityB::class.java).apply {
                putExtra(COUNT_KEY, binding.txtCount.text.toString())
            }
            launcher.launch(intent)
        }

        binding.btnSendText.setSafeOnClickListener {
            shareText("Hello! This is message from my app!")
        }

        binding.btnCheckPermission.setSafeOnClickListener {
            permission.requestPhotoPermissionWithRetry(this)
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt(COUNT_KEY, cnt)
//    }
//
//    fun returnState(savedInstanceState: Bundle?) {
//        savedInstanceState?.let {
//            cnt = it.getInt(COUNT_KEY)
//            binding.txtCount.text = cnt.toString()
//        }
//    }

    fun sendObject() {
        binding.btnParcelable.setOnClickListener {
            sendWithParcelable()
        }

        binding.btnSerializable.setOnClickListener {
            sendWithSerializable()
        }

        binding.btnJson.setOnClickListener {
            sendUseJson()
        }

        binding.btnViewModel.setOnClickListener {
            sendWithViewModel()
        }

        binding.btnEventBus.setOnClickListener {
            sendWithEventBus()
        }
    }

    fun sendWithParcelable() {
        val id = binding.edtId.text.toString()
        val name = binding.edtName.text.toString()

        if (id.isNotEmpty() && name.isNotEmpty()) {
            val user = User1(id, name)

            val intent = Intent(this, ActivityB::class.java)
            intent.putExtra("user1", user)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter information fully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendWithSerializable() {
        val id = binding.edtId.text.toString()
        val name = binding.edtName.text.toString()

        if (id.isNotEmpty() && name.isNotEmpty()) {
            val user = User2(id, name)

            val intent = Intent(this, ActivityB::class.java)
            intent.putExtra("user2", user)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter information fully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendUseJson() {
        val id = binding.edtId.text.toString()
        val name = binding.edtName.text.toString()

        if (id.isNotEmpty() && name.isNotEmpty()) {
            val user = User(id, name)

            val gson = Gson()
            val userJson = gson.toJson(user)

            val intent = Intent(this, ActivityB::class.java)
            intent.putExtra("user", userJson)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter information fully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendWithViewModel() {
        val id = binding.edtId.text.toString()
        val name = binding.edtName.text.toString()

        val app = application as MyApp
        if (id.isNotEmpty() && name.isNotEmpty()) {
            val user = User(id, name)
            app.sharedViewModel.setUser(user)
        }

        val intent = Intent(this, ActivityB::class.java)
        startActivity(intent)
    }

    private fun sendWithEventBus() {
        val id = binding.edtId.text.toString()
        val name = binding.edtName.text.toString()

        if (id.isNotEmpty() && name.isEmpty()) {
            val user = User(id, name)
            EventBus.getDefault().postSticky(user)
            val intent = Intent(this, ActivityB::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter information fully!", Toast.LENGTH_SHORT)
        }
    }

    private fun shareText(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Share with..."))
        } else {
            Toast.makeText(this, "No app can share", Toast.LENGTH_SHORT).show()
            Log.w("ShareText", "No app can solve ACTION_SEND")
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