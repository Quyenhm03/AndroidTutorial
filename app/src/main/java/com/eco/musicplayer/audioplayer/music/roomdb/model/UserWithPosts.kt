package com.eco.musicplayer.audioplayer.music.roomdb.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithPosts(
    @Embedded
    val user: User,

    @Relation(parentColumn = "id", entityColumn = "userId")
    val posts: List<Post>
)