package com.wanotube.wanotubeapp.domain

import com.wanotube.wanotubeapp.util.smartTruncate
import java.util.Date

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

/**
 * Videos represent a WanoTube video that can be displayed
 */
data class Video(
    val id: String,
    val url: String,
    val title: String,
    val description: String,
    var thumbnail: String,
    val size: Long,
    val totalViews: Long,
    val totalLikes: Long,
    val totalComments: Long,
    val visibility: Int,
    val duration: Int,
    val authorId: String,
    val type: String,
    val recognitionResultTitle: String?,
    val recognitionResultAlbum: String?,
    val recognitionResultArtist: String?,
    val recognitionResultLabel: String?,
    val createdAt: Date,
    val updatedAt: Date 
) {
    /**
     * Short description is used for displaying truncated descriptions in the UI
     */
    val shortDescription: String
        get() = description.smartTruncate(200)
}

data class User (
    val id: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthDate: Date,
    val phoneNumber: String,
    val country: String,
    val description: String)

data class Account (
    val id: String,
    val username: String,
    val isAdmin: Boolean,
    val avatar: String,
    val userId: String)

data class Comment (
    val id: String,
    val content: String,
    val authorId: String,
    val authorUsername: String,
    val authorAvatar: String,
    val videoId: String)