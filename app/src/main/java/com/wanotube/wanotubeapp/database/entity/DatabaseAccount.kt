package com.wanotube.wanotubeapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseAccount constructor(
    @PrimaryKey
    val id: String,
    val username: String,
    val isAdmin: Boolean,
    var avatar: String,
    val userId: String)

data class DatabaseChannel constructor(
    val username: String,
    val channelId: String,
    var avatar: String,
    val numberOfFollowers: Int)
