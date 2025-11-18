package com.eco.musicplayer.audioplayer.music.roomdb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _providerResult = MutableLiveData<String>()
    val providerResult: LiveData<String> = _providerResult

    fun addUserAndPost(user: User, title: String, content: String) {
        viewModelScope.launch { repo.addUserAndPost(user, title, content) }
    }

    fun clearAll() {
        viewModelScope.launch { repo.clearAll() }
    }

    fun queryViaProvider() {
        viewModelScope.launch {
            try {
                val users = repo.queryUsersViaContentProvider()
                _providerResult.value = "Content Provider Query:\n" +
                        users.joinToString("\n") { "- ${it.name} (${it.email})"}
            } catch (e: Exception) {
                _providerResult.value = "Erro: ${e.message}"
            }
        }
    }

    fun insertViaProvider(name: String, email: String) {
        viewModelScope.launch {
            try {
                val uri = repo.insertUserViaContentProvider(name, email)
                _providerResult.value = "Inserted via Provider: $uri"
            } catch (e: Exception) {
                _providerResult.value = "Error: ${e.message}"
            }
        }
    }

    fun bulkInsertViaProvider() {
        viewModelScope.launch {
            try {
                val testUsers = listOf(
                    "Bulk User 1" to "bulk1@test.com",
                    "Bulk User 2" to "bulk2@test.com",
                    "Bulk User 3" to "bulk3@test.com"
                )
                val count = repo.bulkInsertUsersViaContentProvider(testUsers)
                _providerResult.value = "Bulk inserted $count users via Provider"
            } catch (e: Exception) {
                _providerResult.value = "Error: ${e.message}"
            }
        }
    }
}