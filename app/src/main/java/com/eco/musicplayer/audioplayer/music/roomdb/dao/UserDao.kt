package com.eco.musicplayer.audioplayer.music.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eco.musicplayer.audioplayer.music.roomdb.model.User
import com.eco.musicplayer.audioplayer.music.roomdb.model.UserPostJoin
import com.eco.musicplayer.audioplayer.music.roomdb.model.UserWithPosts
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAbort(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsersFlow(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    // Transactional relation fetch (One-to-Many)
    @Transaction
    @Query("SELECT * FROM users WHERE id = :uid")
    fun getUserWithPostsFlow(uid: Int): Flow<UserWithPosts>

    @Transaction
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getUsersWithPosts(): Flow<List<UserWithPosts>>

    @Query("""
        SELECT users.full_name AS username, users.email AS userEmail, posts.title AS postTitle
        FROM users INNER JOIN posts
        ON users.id = posts.userId
        ORDER BY users.id DESC
    """)
    fun getUserPostJoin(): Flow<List<UserPostJoin>>

    @Query("SELECT * FROM users WHERE full_name LIKE :namePattern")
    fun findByName(namePattern: String): Flow<List<User>>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}