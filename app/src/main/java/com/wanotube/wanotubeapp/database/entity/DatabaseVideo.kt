package com.wanotube.wanotubeapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.wanotube.wanotubeapp.domain.Video
import java.util.Date

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

data class DatabaseWatchHistory constructor(
    val id: String,
    val accountId: String,
    val date: String,
    val videos: List<Video>
)