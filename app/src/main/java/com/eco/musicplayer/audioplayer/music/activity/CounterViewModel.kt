package com.eco.musicplayer.audioplayer.music.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {

    private val counter = CounterModel()

    private val _count = MutableLiveData(counter.count)
    val count: LiveData<Int> = _count

    fun increment() {
        counter.count++
        _count.value = counter.count
    }

    fun decrement() {
        counter.count--
        _count.value = counter.count
    }
}