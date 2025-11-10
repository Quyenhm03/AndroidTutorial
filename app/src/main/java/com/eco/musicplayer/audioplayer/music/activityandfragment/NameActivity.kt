package com.eco.musicplayer.audioplayer.music.activityandfragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityConnectFragmentBinding

class NameActivity: AppCompatActivity(), NameFragment.OnNameListener {

    private lateinit var binding: ActivityConnectFragmentBinding
    private lateinit var nameViewModel: NameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConnectFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameViewModel = ViewModelProvider(this)[NameViewModel::class.java]
        nameViewModel.name.observe(this) { name ->
           binding.txtName.text = "Name: $name"
        }

        binding.btnUseCallback.setOnClickListener { loadFragment(true) }
        binding.btnUseViewModel.setOnClickListener { loadFragment(false) }
    }

    fun loadFragment(useCallback: Boolean) {
        val fragment = NameFragment.newInstance(useCallback)
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
        }

        binding.txtName.text = if (useCallback)
            "Connect with Callback"
        else
            "Connect with ViewModel"

        if (!useCallback) {
            nameViewModel.sendMsgToFragment("Name default: Nguyen Van A")
        }
    }

    override fun onNameEntered(name: String) {
        binding.txtName.text = "Name: $name"
    }
}