package com.wanotube.wanotubeapp.domain

import com.wanotube.wanotubeapp.util.smartTruncate
import java.util.*

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
data class WanoTubeVideo(
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
    val authorId: String
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
    val avatar: String,
    val description: String) {

}