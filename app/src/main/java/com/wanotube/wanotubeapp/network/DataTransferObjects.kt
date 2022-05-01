package com.wanotube.wanotubeapp.network

import com.google.gson.annotations.SerializedName
import com.wanotube.wanotubeapp.database.DatabaseAccount
import com.wanotube.wanotubeapp.database.DatabaseVideo

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
    var _id: String = ""
    @SerializedName("url")
    val url: String = ""
    @SerializedName("title")
    val title: String = ""
    @SerializedName("updated")
    val updated: String = ""
    @SerializedName("description")
    val description: String  = ""
    @SerializedName("thumbnail_url")
    val thumbnail: String = ""
    @SerializedName("size")
    val size: Long = 0
    @SerializedName("total_views")
    val total_views: Long = 0
    @SerializedName("total_likes")
    val total_likes: Long = 0
    @SerializedName("total_comments")
    val total_comments: Long = 0
    @SerializedName("visibility")
    val visibility: Int = 0
    @SerializedName("duration")
    val duration: String = ""
    @SerializedName("author_id")
    val author_id: String = ""
    @SerializedName("type")
    val type: String = ""
    @SerializedName("created_at")
    val created_at: String = ""
    @SerializedName("updated_at")
    val updated_at: String = ""
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
            id = it._id,
            url = it.url,
            title = it.title,
            description = it.description,
            updated = it.updated,
            thumbnail = it.thumbnail,
            size = it.size,
            totalViews = it.total_views,
            totalLikes = it.total_likes,
            totalComments = it.total_comments,
            visibility = it.visibility,
            duration = it.duration,
            authorId = it.author_id
        )
    }
}

fun NetworkVideo.asDatabaseModel(): DatabaseVideo {
    return DatabaseVideo(
        id = _id,
        url = url,
        title = title,
        description = description,
        updated = updated,
        thumbnail = thumbnail,
        size = size,
        totalViews = total_views,
        totalLikes = total_likes,
        totalComments = total_comments,
        visibility = visibility,
        duration = duration,
        authorId = author_id
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