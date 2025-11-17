package com.eco.musicplayer.audioplayer.music.roomdb.repository

import com.eco.musicplayer.audioplayer.music.roomdb.dao.PostDao
import com.eco.musicplayer.audioplayer.music.roomdb.dao.UserDao
import com.eco.musicplayer.audioplayer.music.roomdb.model.Post
import com.eco.musicplayer.audioplayer.music.roomdb.model.User
import com.eco.musicplayer.audioplayer.music.roomdb.model.UserPostJoin
import com.eco.musicplayer.audioplayer.music.roomdb.model.UserWithPosts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val postDao: PostDao
) {

    fun getAllUsersFlow(): Flow<List<User>> = userDao.getAllUsersFlow()
    fun getUsersWithPosts(): Flow<List<UserWithPosts>> = userDao.getUsersWithPosts()
    fun getUserCountFlow() = userDao.getUserCount()
    fun getUserPostJoinFlow(): Flow<List<UserPostJoin>> = userDao.getUserPostJoin()

    suspend fun addUserAndPost(user: User, postTitle: String, postContent: String) {
        withContext(Dispatchers.IO) {
            val userId = userDao.insertReplace(user).toInt()
            val post = Post(title = postTitle, content = postContent, userId = userId)
            postDao.insertPost(post)
        }
    }

    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            postDao.clearAllPosts()
            userDao.deleteAllUsers()
        }
    }
}