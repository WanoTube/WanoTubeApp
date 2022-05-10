package com.wanotube.wanotubeapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class DatabaseUser constructor(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthDate: Date,
    val phoneNumber: String,
    val country: String,
    val avatar: String,
    val description: String)