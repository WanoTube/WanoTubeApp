package com.wanotube.wanotubeapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DatabaseVideo represents a video entity in the database
 */
@Entity
data class DatabaseVideo constructor(
    @PrimaryKey
    val id: String,
    val url: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val size: Long,
    val totalViews: Long,
    val totalLikes: Long,
    val totalComments: Long,
    val visibility: Int,
    val duration: Int,
    val authorId: String,
    val type: String,
    val createdAt: String,
    val updatedAt: String
)