package com.wanotube.wanotubeapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.domain.User
import com.wanotube.wanotubeapp.domain.Video
import java.util.Date

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
    val description: String,
    val thumbnail: String,
    val size: Long,
    val totalViews: Long,
    val totalLikes: Long,
    val totalComments: Long,
    val visibility: Int,
    val duration: Int,
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
data class DatabaseAccount constructor(
    @PrimaryKey
    val id: String,
    val username: String,
    val isAdmin: Boolean,
    val avatar: String,
    val channelId: String)

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
fun List<DatabaseVideo>.asDomainModel(): List<Video> {
    return map {
        Video(
            id = it.id,
            url = it.url,
            title = it.title,
            description = it.description,
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

fun DatabaseVideo.asDomainModel(): Video {
    return Video(
        id = id,
        url = url,
        title = title,
        description = description,
        thumbnail = thumbnail,
        size = size,
        totalViews = totalViews,
        totalLikes = totalLikes,
        totalComments = totalComments,
        visibility = visibility,
        duration = duration,
        authorId = authorId)
}

fun DatabaseAccount.asDomainModel(): Account {
    return Account(
        id = id,
        username = username,
        isAdmin = isAdmin,
        avatar = avatar,
        channelId = channelId
    )
}