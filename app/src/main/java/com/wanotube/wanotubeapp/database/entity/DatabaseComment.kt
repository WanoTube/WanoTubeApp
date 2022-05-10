package com.wanotube.wanotubeapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseComment constructor(
    @PrimaryKey
    val id: String,
    val content: String,
    val authorId: String,
    val authorUsername: String? = "",
    val authorAvatar: String? = "",
    val videoId: String)