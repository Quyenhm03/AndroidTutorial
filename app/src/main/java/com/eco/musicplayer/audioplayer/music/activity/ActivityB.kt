package com.eco.musicplayer.audioplayer.music.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.eco.musicplayer.audioplayer.music.databinding.ActivityBBinding
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ActivityB : BaseNetworkActivity() {
    private val TAG = "ActivityB"
    private val COUNT_KEY = "count_key"

    private lateinit var binding: ActivityBBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "OnCreate")

        val extras = intent.extras
        val count = extras?.getString(COUNT_KEY, "0") ?: "0"
        val countStr = extras?.getString("count_str") ?: "No string received"

        binding.txtReceive.text = count.toString()

        receiveUserData()
        onClick()
    }

    fun receiveUserData() {
        // Parcelable
        val user1 = intent.getParcelableExtra<User1>("user1")
        if (user1 != null) {
            binding.txtReceive.text = "Parcelable: $user1"
            return
        }

        // Serializable
        val user2 = intent.getSerializableExtra("user2") as? User2
        if (user2 != null) {
            binding.txtReceive.text = "Serializable: $user2"
            return
        }

        // JSON
        val userJson = intent.getStringExtra("user")
        if (userJson != null) {
            val user = Gson().fromJson(userJson, User::class.java)
            binding.txtReceive.text = "JSON: $user"
            return
        }

        // ViewModel
        val app = application as MyApp
        app.sharedViewModel.user.observe(this) { user ->
            if (user != null) {
                binding.txtReceive.text = "ViewModel: $user"
            }
        }
    }

    fun onClick() {
        binding.btnReturn.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            val intent = Intent(this, ActivityA::class.java)
            intent.putExtra("result", binding.edtInput.text.toString())

            setResult(RESULT_OK, intent)
            finish()
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUserEvent(event: User) {
        binding.txtReceive.text = event.toString()
        EventBus.getDefault().removeStickyEvent(event)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
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
        EventBus.getDefault().unregister(this)
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