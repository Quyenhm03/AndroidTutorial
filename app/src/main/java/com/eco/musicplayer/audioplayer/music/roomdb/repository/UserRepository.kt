package com.eco.musicplayer.audioplayer.music.roomdb.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import androidx.annotation.ContentView
import com.eco.musicplayer.audioplayer.music.roomdb.contentprovider.UserPostProvider
import com.eco.musicplayer.audioplayer.music.roomdb.dao.PostDao
import com.eco.musicplayer.audioplayer.music.roomdb.dao.UserDao
import com.eco.musicplayer.audioplayer.music.roomdb.model.Post
import com.eco.musicplayer.audioplayer.music.roomdb.model.User
import com.eco.musicplayer.audioplayer.music.roomdb.model.UserData
import com.eco.musicplayer.audioplayer.music.roomdb.model.UserPostJoin
import com.eco.musicplayer.audioplayer.music.roomdb.model.UserWithPosts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val context: Context
) {

    private val contentResolver = context.contentResolver

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

    suspend fun queryUsersViaContentProvider(): List<UserData> = withContext(Dispatchers.IO) {
        val cursor = contentResolver.query(
            UserPostProvider.USERS_URI,
            null, null, null, null
        )

        val users = mutableListOf<UserData>()
        cursor?.use {
            val idIdx = it.getColumnIndex("id")
            val nameIdx = it.getColumnIndex("full_name")
            val emailIdx = it.getColumnIndex("email")

            while (it.moveToNext()) {
                users.add(
                    UserData(
                        id = it.getInt(idIdx),
                        name = it.getString(nameIdx),
                        email = it.getString(emailIdx)
                    )
                )
            }
        }
        users
    }

    suspend fun insertUserViaContentProvider(name: String, email: String) : Uri? =
        withContext(Dispatchers.IO) {
            val values = ContentValues().apply {
                put("full_name", name)
                put("email", email)
                put("street", "")
                put("city", "")
            }
            contentResolver.insert(UserPostProvider.USERS_URI, values)
        }

    suspend fun updateUserViaContentProvider(id: Int, name: String, email: String): Int =
        withContext(Dispatchers.IO) {
            val values = ContentValues().apply {
                put("full_name", name)
                put("email", email)
            }
            val uri = ContentUris.withAppendedId(UserPostProvider.USERS_URI, id.toLong())
            contentResolver.update(uri, values, null, null)
        }

    suspend fun deleteUserViaContentProvider(id: Int): Int = withContext(Dispatchers.IO) {
        val uri = ContentUris.withAppendedId(UserPostProvider.USERS_URI, id.toLong())
        contentResolver.delete(uri, null, null)
    }

    suspend fun bulkInsertUsersViaContentProvider(users: List<Pair<String, String>>): Int =
        withContext(Dispatchers.IO) {
            val values = users.map { (name, email) ->
                ContentValues().apply {
                    put("full_name", name)
                    put("email", email)
                    put("street", "")
                    put("city", "")
                }
            }.toTypedArray()

            contentResolver.bulkInsert(UserPostProvider.USERS_URI, values)
        }
}