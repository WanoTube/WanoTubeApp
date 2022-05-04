package com.wanotube.wanotubeapp.network

import com.google.gson.annotations.SerializedName
import com.wanotube.wanotubeapp.database.DatabaseAccount
import com.wanotube.wanotubeapp.database.DatabaseVideo
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */


/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
class NetworkVideoContainer {
    @SerializedName("videos")
    var videos: List<NetworkVideo> = listOf()
}

/**
 * Videos represent a WanoTube video that can be displayed
 */
class NetworkVideo {
    @SerializedName("_id")
    var id: String = ""
    @SerializedName("url")
    val url: String = ""
    @SerializedName("title")
    val title: String = ""
    @SerializedName("description")
    val description: String  = ""
    @SerializedName("thumbnail_url")
    val thumbnail: String = ""
    @SerializedName("size")
    val size: Long = 0
    @SerializedName("total_views")
    val totalViews: Long = 0
    @SerializedName("total_likes")
    val totalLikes: Long = 0
    @SerializedName("total_comments")
    val totalComments: Long = 0
    @SerializedName("visibility")
    val visibility: Int = 0
    @SerializedName("duration")
    val duration: Int = 0
    @SerializedName("author_id")
    val authorId: String = ""
    @SerializedName("type")
    val type: String = "NORMAL"
    @SerializedName("created_at")
    val createdAt: String = ""
    @SerializedName("updated_at")
    val updatedAt: String = ""
}

class NetworkUser {
    @SerializedName("email")
    val email: String = ""
    @SerializedName("password")
    val password: String = ""
}

class NetworkAccount {
    @SerializedName("_id")
    val id: String = ""
    @SerializedName("username")
    val username: String = ""
    @SerializedName("is_admin")
    val isAdmin: Boolean = false
    @SerializedName("avatar")
    val avatar: String = ""
    @SerializedName("channel_id")
    val channelId: String = ""
    @SerializedName("email")
    val email: String = ""
}

class LoginResult {
    @SerializedName("token")
    val token: String = ""
    @SerializedName("user")
    val user: NetworkAccount? = null
}
/**
 * Convert Network results to database objects
 */
fun NetworkVideoContainer.asDatabaseModel(): List<DatabaseVideo> {
    return videos.map {
        DatabaseVideo(
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
            updatedAt = it.updatedAt,
            createdAt = it.createdAt
        )
    }
}

fun NetworkVideo.asDatabaseModel(): DatabaseVideo {
    return DatabaseVideo(
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
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

fun NetworkAccount.asDatabaseModel(): DatabaseAccount {
    return DatabaseAccount(
        id = id,
        username = username,
        avatar = avatar,
        channelId = channelId,
        isAdmin = isAdmin
    )
}