package com.wanotube.wanotubeapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wanotube.wanotubeapp.domain.WanoTubeVideo
import java.util.*

/**
 * Database entities go in this file. These are responsible for reading and writing
 * from the database.
 */


/**
 * DatabaseVideo represents a video entity in the database
 */
@Entity
data class DatabaseVideo constructor(
    @PrimaryKey
    val id: String,
    val url: String,
    val title: String,
    val updated: String,
    val description: String,
    val thumbnail: String,
    val size: Long,
    val totalViews: Long,
    val totalLikes: Long,
    val totalComments: Long,
    val visibility: Int,
    val duration: String,
    val authorId: String)

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

@Entity
data class DatabaseComment constructor(
    @PrimaryKey
    val id: String,
    val content: String,
    val authorId: String,
    val videoId: String)

/**
 * Map DatabaseVideos to domain entities
 */
fun List<DatabaseVideo>.asDomainModel(): List<WanoTubeVideo> {
    return map {
        WanoTubeVideo(
            id = it.id,
            url = it.url,
            title = it.title,
            description = it.description,
            updated = it.updated,
            thumbnail = it.thumbnail,
            size = it.size,
            totalViews = it.totalViews,
            totalLikes = it.totalLikes,
            totalComments = it.totalComments,
            visibility = it.visibility,
            duration = it.duration,
            authorId = it.authorId)
    }
}

