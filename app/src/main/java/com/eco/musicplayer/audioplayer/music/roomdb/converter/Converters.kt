package com.eco.musicplayer.audioplayer.music.roomdb.converter

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    // Date <-> Long
    @TypeConverter
    fun fromTimestamp(value: Long?): Date?= value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromStringList(value: String?): List<String> =
        value?.split("|")?.filter { it.isNotEmpty() } ?: emptyList()

    @TypeConverter
    fun toStringList(list: List<String>?): String = list?.joinToString("|") ?: ""
}