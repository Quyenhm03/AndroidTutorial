package com.eco.musicplayer.audioplayer.music.roomdb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eco.musicplayer.audioplayer.music.roomdb.model.User
import com.eco.musicplayer.audioplayer.music.roomdb.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repo: UserRepository) : ViewModel() {

    val users = repo.getUsersWithPosts().asLiveData()
    val count = repo.getUserCountFlow().asLiveData()
    val joinData = repo.getUserPostJoinFlow().asLiveData()

    fun addUserAndPost(user: User, title: String, content: String) {
        viewModelScope.launch { repo.addUserAndPost(user, title, content) }
    }

    fun clearAll() {
        viewModelScope.launch { repo.clearAll() }
    }
}