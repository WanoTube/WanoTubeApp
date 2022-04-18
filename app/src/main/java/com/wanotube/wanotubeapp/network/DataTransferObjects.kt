package com.wanotube.wanotubeapp.network

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
data class NetworkVideoContainer(val videos: List<NetworkVideo>)

/**
 * Videos represent a WanoTube video that can be displayed
 */
data class NetworkVideo(
    val _id: String,
    val url: String,
    val title: String,
    val updated: String,
    val description: String,
    val thumbnail: String,
    val size: Long,
    val total_views: Long,
    val total_likes: Long,
    val total_comments: Long,
    val visibility: Int,
    val duration: String,
    val author_id: String,
    val type: String,
    val created_at: String,
    val updated_at: String)

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
            authorId = it.author_id)
    }
}