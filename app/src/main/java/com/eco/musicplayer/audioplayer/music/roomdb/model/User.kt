package com.eco.musicplayer.audioplayer.music.roomdb.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "full_name")
    val name: String,

    val email: String,

    @Embedded
    val address: Address = Address()
) {
    @Ignore
    var tempNote: String = ""
}