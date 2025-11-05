package com.eco.musicplayer.audioplayer.music.activity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class User1(val id: String, val name: String) : Parcelable

data class User2(val id: String, val name: String) : Serializable

data class User(val id: String, val name: String)