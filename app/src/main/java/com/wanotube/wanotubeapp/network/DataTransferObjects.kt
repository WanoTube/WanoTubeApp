package com.wanotube.wanotubeapp.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.wanotube.wanotubeapp.database.DatabaseAccount
import com.wanotube.wanotubeapp.database.DatabaseUser
import com.wanotube.wanotubeapp.database.DatabaseVideo
import com.wanotube.wanotubeapp.util.convertStringToDate
import java.util.Date

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

class NetworkVideoWatch {
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
    @SerializedName("user")
    var user: UserWatch? = null    
    @SerializedName("type")
    val type: String = "NORMAL"
    @SerializedName("created_at")
    val createdAt: String = ""
    @SerializedName("updated_at")
    val updatedAt: String = ""
}

class NetworkAccount {
    @SerializedName("_id")
    @Expose
    var id: String = ""

    @SerializedName("username")
    @Expose
    var username: String = ""

    @SerializedName("email")
    @Expose
    var email: String = ""

    @SerializedName("is_admin")
    @Expose
    var isAdmin: Boolean = false

    @SerializedName("user_id")
    @Expose
    var userId: UserId? = null

    @SerializedName("followers")
    @Expose
    var followers: List<Any>? = null

    @SerializedName("followings")
    @Expose
    var followings: List<Any>? = null

    @SerializedName("members")
    @Expose
    var members: List<Any>? = null

    @SerializedName("blocked_accounts")
    @Expose
    var blockedAccounts: List<Any>? = null

    @SerializedName("watched_history")
    @Expose
    var watchedHistory: List<Any>? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("__v")
    @Expose
    var v: Int? = null
}

class LoginResult {
    @SerializedName("token")
    val token: String = ""
    @SerializedName("user")
    val user: NetworkAccount? = null
}

class UserResult {
    @SerializedName("user")
    @Expose
    var user: NetworkUser? = null

    @SerializedName("channel_id")
    @Expose
    var channelId: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null
}

class NetworkUser {
    @SerializedName("_id")
    @Expose
    var id: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("birth_date")
    @Expose
    var birthDate: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("__v")
    @Expose
    var v: Int? = null
}

class UserId {
    @SerializedName("_id")
    @Expose
    var id: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("birth_date")
    @Expose
    var birthDate: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("__v")
    @Expose
    var v: Int? = null
}


class UserWatch {
    @SerializedName("avatar")
    @Expose
    var avatar: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("channel_id")
    @Expose
    var channelId: String? = null
    @SerializedName("_doc")
    @Expose
    var doc: Doc? = null
}

class Doc {
    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("is_admin")
    @Expose
    var isAdmin: String? = null
    @SerializedName("user_id")
    @Expose
    var user: UserId? = null
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
        avatar = userId?.avatar.toString(),
        userId = userId?.id.toString(),
        isAdmin = isAdmin
    )
}

fun NetworkVideoWatch.asDatabaseModel(): DatabaseVideo {
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
        authorId = "",
        type = type,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

fun UserId.asDatabaseModel(): DatabaseUser {
    return DatabaseUser(
        id = id.toString(),
        firstName = firstName.toString(),
        lastName = lastName.toString(),
        gender = gender.toString(),
        birthDate = birthDate?.let { convertStringToDate(it) } ?: Date(),
        phoneNumber = phoneNumber.toString(),
        country = country.toString(),
        avatar = avatar.toString(),
        description = ""
    )
}
