package com.wanotube.wanotubeapp.database

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wanotube.wanotubeapp.domain.Account
import com.wanotube.wanotubeapp.domain.Video
import com.wanotube.wanotubeapp.util.SourceTypeConverters
import com.wanotube.wanotubeapp.util.convertStringToDate
import java.util.Date

/**
 * Database entities go in this file. These are responsible for reading and writing
 * from the database.
 */


/**
 * DatabaseVideo represents a video entity in the database
 */
@TypeConverters(SourceTypeConverters::class)
@Entity(foreignKeys = [ForeignKey(
    entity = DatabaseAccount::class, 
    parentColumns = arrayOf("id"), 
    childColumns = arrayOf("user"),
    onDelete = ForeignKey.CASCADE)
])
data class DatabaseVideo constructor(
    @ColumnInfo
    @PrimaryKey
    val id: String,
    @ColumnInfo
    val url: String,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val description: String,
    @ColumnInfo
    val thumbnail: String,
    @ColumnInfo
    val size: Long,
    @ColumnInfo
    val totalViews: Long,
    @ColumnInfo
    val totalLikes: Long,
    @ColumnInfo
    val totalComments: Long,
    @ColumnInfo
    val visibility: Int,
    @ColumnInfo
    val duration: Int,
    @ColumnInfo
    val authorId: String,
    @ColumnInfo(index = true)
    var user: DatabaseAccount? = null,
    @ColumnInfo
    val type: String,
    @ColumnInfo
    val createdAt: String,
    @ColumnInfo
    val updatedAt: String
)

@Entity
data class DatabaseUser constructor(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthDate: Date,
    val phoneNumber: String,
    val country: String,
    val avatar: String,
    val description: String)

@Entity
data class DatabaseAccount constructor(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    val username: String,
    val isAdmin: Boolean,
    val avatar: String,
    val channelId: String)

@Entity
data class DatabaseComment constructor(
    @PrimaryKey
    val id: String,
    val content: String,
    val authorId: String,
    val videoId: String)

/**
 * Map DatabaseVideos to domain entities
 */
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
            createdAt = convertStringToDate(it.createdAt),
            updatedAt = convertStringToDate(it.updatedAt),
            user = (it.user?: it.user?.asDomainModel()) as Account
        )
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
        user = (user?: user?.asDomainModel()) as Account,
        type = type,
        createdAt = convertStringToDate(createdAt),
        updatedAt = convertStringToDate(updatedAt))
}

fun DatabaseAccount.asDomainModel(): Account {
    return Account(
        id = id,
        username = username,
        isAdmin = isAdmin,
        avatar = avatar,
        channelId = channelId
    )
}