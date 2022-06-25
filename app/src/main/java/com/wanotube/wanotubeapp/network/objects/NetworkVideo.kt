package com.wanotube.wanotubeapp.network.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.wanotube.wanotubeapp.domain.Video

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

class NetworkVideoWatchContainer {
    @SerializedName("videos")
    var videos: List<NetworkVideoWatch> = listOf()
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
    @SerializedName("recognition_result")
    var recognitionResult: NetworkRecognitionResult? = null
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
    @SerializedName("recognition_result")
    var recognitionResult: NetworkRecognitionResult? = null
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
    @SerializedName("avatar")
    @Expose
    var avatar: String? = null
}

class NetworkWatchHistoryDate {

    @SerializedName("_id")
    @Expose
    var id: String? = null
    
    @SerializedName("account_id")
    @Expose
    var accountId: String? = null
    
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("videos")
    @Expose
    var videos: List<Video> = listOf()
}

class NetworkWatchHistoryContainer {

    @SerializedName("history")
    @Expose
    var history: List<NetworkWatchHistoryDate> = listOf()
}