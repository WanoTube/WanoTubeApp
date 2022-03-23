package com.wanotube.wanotubeapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wanotube.wanotubeapp.domain.WanoTubeVideo

/**
 * Database entities go in this file. These are responsible for reading and writing
 * from the database.
 */


/**
 * DatabaseVideo represents a video entity in the database
 */
@Entity
data class DatabaseVideo constructor(
    @PrimaryKey
    val url: String,
    val updated: String,
    val title: String,
    val description: String,
    val thumbnail: String)

/**
 * Map DatabaseVideos to domain entities
 */
fun List<DatabaseVideo>.asDomainModel(): List<WanoTubeVideo> {
    return map {
        WanoTubeVideo(
            url = it.url,
            title = it.title,
            description = it.description,
            updated = it.updated,
            thumbnail = it.thumbnail)
    }
}