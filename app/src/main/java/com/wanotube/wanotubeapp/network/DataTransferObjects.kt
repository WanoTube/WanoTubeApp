package com.wanotube.wanotubeapp.network

import com.wanotube.wanotubeapp.database.entity.DatabaseAccount
import com.wanotube.wanotubeapp.database.entity.DatabaseChannel
import com.wanotube.wanotubeapp.database.entity.DatabaseComment
import com.wanotube.wanotubeapp.database.entity.DatabaseUser
import com.wanotube.wanotubeapp.database.entity.DatabaseVideo
import com.wanotube.wanotubeapp.database.entity.DatabaseWatchHistory
import com.wanotube.wanotubeapp.network.objects.NetworkAccount
import com.wanotube.wanotubeapp.network.objects.NetworkComment
import com.wanotube.wanotubeapp.network.objects.NetworkCommentContainer
import com.wanotube.wanotubeapp.network.objects.NetworkFollowingChannelContainer
import com.wanotube.wanotubeapp.network.objects.NetworkVideo
import com.wanotube.wanotubeapp.network.objects.NetworkVideoContainer
import com.wanotube.wanotubeapp.network.objects.NetworkVideoWatch
import com.wanotube.wanotubeapp.network.objects.NetworkWatchHistoryContainer
import com.wanotube.wanotubeapp.network.objects.UserId
import com.wanotube.wanotubeapp.util.convertSimpleStringToDate
import com.wanotube.wanotubeapp.util.convertStringToDate
import java.util.Date

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

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

fun NetworkComment.asDatabaseModel(): DatabaseComment {
    return DatabaseComment(
        id = id.toString(),
        content = content.toString(),
        authorId = user?.id.toString(), //Note: accountId - channelId
        videoId = videoId.toString(),
        authorAvatar = "",
        authorUsername = user?.username.toString()
    )
}

fun NetworkCommentContainer.asDatabaseModel(): List<DatabaseComment> {
    return comments.map {
        DatabaseComment(
            id = it.id.toString(),
            content = it.content.toString(),
            authorId = it.user?.id.toString(),
            videoId = it.videoId.toString(),
            authorAvatar = "",
            authorUsername = it.user?.username.toString()
        )
    }
}

fun NetworkFollowingChannelContainer.asDatabaseModel(): List<DatabaseChannel> {
    return channels.map {
        DatabaseChannel(
            username = it.username.toString(),
            channelId = it.channelId.toString(),
            avatar = it.avatar.toString(),
            numberOfFollowers = it.numberOfFollowers
        )
    }
}

fun NetworkWatchHistoryContainer.asDatabaseModel(): List<DatabaseWatchHistory> {

    return history.map {
        DatabaseWatchHistory(
            id = it.id.toString(),
            accountId = it.accountId.toString(),
            date = it.date.toString(),
            videos = it.videos
        )
    }
}