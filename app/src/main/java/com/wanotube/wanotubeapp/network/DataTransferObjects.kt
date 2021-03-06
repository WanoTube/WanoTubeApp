package com.wanotube.wanotubeapp.network

import com.wanotube.wanotubeapp.database.entity.DatabaseAccount
import com.wanotube.wanotubeapp.database.entity.DatabaseChannel
import com.wanotube.wanotubeapp.database.entity.DatabaseComment
import com.wanotube.wanotubeapp.database.entity.DatabaseUser
import com.wanotube.wanotubeapp.database.entity.DatabaseVideo
import com.wanotube.wanotubeapp.database.entity.DatabaseWatchHistory
import com.wanotube.wanotubeapp.network.objects.*
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
        val musicRecognitionResult = it.recognitionResult?.metadata?.music
        var album = ""
        var artist = ""
        var title = ""
        var label = ""

        if (musicRecognitionResult?.size != null) {
            if (musicRecognitionResult.isNotEmpty()) {
                val music = musicRecognitionResult[0]
                album = music.album?.name.toString()
                artist = music.artists?.get(0)?.name.toString()
                title = music.title.toString()
                label = music.label.toString()
            }
        }

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
            recognitionResultTitle = title,
            recognitionResultAlbum = album,
            recognitionResultArtist = artist,
            recognitionResultLabel = label,
            updatedAt = it.updatedAt,
            createdAt = it.createdAt
        )
    }
}

fun NetworkVideoWatchContainer.asDatabaseModel(): List<DatabaseVideo> {
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
            authorId = it.user?.channelId.toString(), //AuthorId here's userId
            type = it.type,
            recognitionResultTitle = "",
            recognitionResultAlbum = "",
            recognitionResultArtist = "",
            updatedAt = it.updatedAt,
            createdAt = it.createdAt
        )
    }
}

fun NetworkVideo.asDatabaseModel(): DatabaseVideo {
    val musicRecognitionResult = recognitionResult?.metadata?.music
    var rAlbum = ""
    var rArtist = ""
    var rTitle = ""
    var rLabel = ""

    if (musicRecognitionResult?.size != null) {
        if (musicRecognitionResult.isNotEmpty()) {
            val music = musicRecognitionResult[0]
            rAlbum = music.album?.name.toString()
            rArtist = music.artists?.get(0)?.name.toString()
            rTitle = music.title.toString()
            rLabel = music.label.toString()
        }
    }

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
        recognitionResultTitle = rTitle,
        recognitionResultAlbum = rAlbum,
        recognitionResultArtist = rArtist,
        recognitionResultLabel = rLabel,
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
    val musicRecognitionResult = recognitionResult?.metadata?.music
    var rAlbum = ""
    var rArtist = ""
    var rTitle = ""
    var rLabel = ""

    if (musicRecognitionResult?.size != null) {
        if (musicRecognitionResult.isNotEmpty()) {
            val music = musicRecognitionResult[0]
            rAlbum = music.album?.name.toString()
            rArtist = music.artists?.get(0)?.name.toString()
            rTitle = music.title.toString()
            rLabel = music.label.toString()
        }
    }

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
        recognitionResultTitle = rTitle,
        recognitionResultAlbum = rAlbum,
        recognitionResultArtist = rArtist,
        recognitionResultLabel = rLabel,
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
            channelId = it.channelId,
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

fun NetworkUser.asDatabaseModel(): DatabaseUser {
    return DatabaseUser(
        id = id.toString(),
        firstName = firstName.toString(),
        lastName = lastName.toString(),
        gender = gender.toString(),
        phoneNumber = phoneNumber.toString(),
        country = country.toString(),
        avatar = avatar.toString(),
        description = description?: "",
        birthDate = Date()
    )
}