package com.wanotube.wanotubeapp.network

import com.squareup.moshi.JsonClass
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
@JsonClass(generateAdapter = true)
data class NetworkVideoContainer(val videos: List<NetworkVideo>)

/**
 * Videos represent a WanoTube video that can be displayed
 */
@JsonClass(generateAdapter = true)
data class NetworkVideo(
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
    val authorId: String)

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
            updated = it.updated,
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