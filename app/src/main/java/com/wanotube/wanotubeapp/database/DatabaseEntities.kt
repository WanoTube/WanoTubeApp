package com.wanotube.wanotubeapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.domain.Comment
import com.wanotube.wanotubeapp.domain.User
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.util.convertStringToDate
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
    val authorId: String,
    val type: String,
    val createdAt: String,
    val updatedAt: String
)

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
    var avatar: String,
    val userId: String)

@Entity
data class DatabaseComment constructor(
    @PrimaryKey
    val id: String,
    val content: String,
    val authorId: String,
    val authorUsername: String? = "",
    val authorAvatar: String? = "",
    val videoId: String)

/**
 * Map DatabaseVideos to domain entities
 */
@JvmName("asDomainModelDatabaseVideo")
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
            authorId = it.authorId,
            type = it.type,
            createdAt = convertStringToDate(it.createdAt),
            updatedAt = convertStringToDate(it.updatedAt))
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
        authorId = authorId,
        type = type,
        createdAt = convertStringToDate(createdAt),
        updatedAt = convertStringToDate(updatedAt))
}

fun DatabaseAccount.asDomainModel(): Account {
    return Account(
        id = id,
        username = username,
        isAdmin = isAdmin,
        avatar = avatar,
        userId = userId
    )
}

@JvmName("asDomainModelDatabaseAccount")
fun List<DatabaseAccount>.asDomainModel(): List<Account> {
    return map {
        Account(
            id = it.id,
            username = it.username,
            isAdmin = it.isAdmin,
            avatar = it.avatar,
            userId = it.userId
        )
    }
}

fun DatabaseUser.asDomainModel(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        gender = gender,
        birthDate = birthDate,
        phoneNumber = phoneNumber,
        country = country,
        description = ""
    )
}

@JvmName("asDomainModelDatabaseComment")
fun List<DatabaseComment>.asDomainModel(): List<Comment> {
    return map {
        Comment(
            id = it.id,
            authorId = it.authorId,
            videoId = it.videoId,
            authorUsername = it.authorUsername.toString(),
            authorAvatar = it.authorAvatar.toString(),
            content = it.content)
    }
}

fun DatabaseComment.asDomainModel(): Comment {
    return Comment(
        id = id,
        authorId = authorId,
        videoId = videoId,
        content = content,
        authorUsername = authorUsername.toString(),
        authorAvatar = authorAvatar.toString()
    )
}