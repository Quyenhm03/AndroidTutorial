package com.eco.musicplayer.audioplayer.music.activityandfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NameViewModel : ViewModel()  {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _msgFromActivity = MutableLiveData<String>()
    val msgFromActivity: LiveData<String> get() = _msgFromActivity

    fun setName(name: String) {
        _name.value = name
    }

    fun sendMsgToFragment(msg: String) {
        _msgFromActivity.value = msg
    }
}