package com.eco.musicplayer.audioplayer.music.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eco.musicplayer.audioplayer.music.roomdb.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    @Update
    suspend fun updatePost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("SELECT * FROM posts WHERE userId = :uid ORDER BY created_at DESC")
    fun getPostsByUser(uid: Int): Flow<List<Post>>

    @Query("SELECT AVG(LENGTH(content)) FROM posts")
    fun getAveragePostLength(): Flow<Double?>

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()
}