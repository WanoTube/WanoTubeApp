package com.wanotube.wanotubeapp.database

import com.wanotube.wanotubeapp.database.entity.DatabaseAccount
import com.wanotube.wanotubeapp.database.entity.DatabaseComment
import com.wanotube.wanotubeapp.database.entity.DatabaseUser
import com.wanotube.wanotubeapp.database.entity.DatabaseVideo
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.domain.Comment
import com.wanotube.wanotubeapp.domain.User
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.util.convertStringToDate

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
            recognitionResultTitle = it.recognitionResultTitle.toString(),
            recognitionResultAlbum = it.recognitionResultAlbum.toString(),
            recognitionResultArtist = it.recognitionResultArtist.toString(),
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
        recognitionResultTitle = recognitionResultTitle.toString(),
        recognitionResultAlbum = recognitionResultAlbum.toString(),
        recognitionResultArtist = recognitionResultArtist.toString(),
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