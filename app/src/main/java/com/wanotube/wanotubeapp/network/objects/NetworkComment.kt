package com.wanotube.wanotubeapp.network.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NetworkComment {
    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("content")
    @Expose
    var content: String? = null
    @SerializedName("user")
    @Expose
    var user: NetworkCommentUser? = null
    @SerializedName("video_id")
    @Expose
    var videoId: String? = null
}

class NetworkCommentUser {
    @SerializedName("_id")
    @Expose
    var id: String = ""

    @SerializedName("username")
    @Expose
    var username: String = ""

    @SerializedName("email")
    @Expose
    var email: String = ""

    @SerializedName("is_admin")
    @Expose
    var isAdmin: Boolean = false

    @SerializedName("user_id")
    @Expose
    var userId: String? = null //This is special case from server result

    @SerializedName("followers")
    @Expose
    var followers: List<Any>? = null

    @SerializedName("followings")
    @Expose
    var followings: List<Any>? = null

    @SerializedName("members")
    @Expose
    var members: List<Any>? = null

    @SerializedName("blocked_accounts")
    @Expose
    var blockedAccounts: List<Any>? = null

    @SerializedName("watched_history")
    @Expose
    var watchedHistory: List<Any>? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("__v")
    @Expose
    var v: Int? = null
}

class NetworkCommentContainer {
    @SerializedName("comments")
    @Expose
    var comments: List<NetworkComment> = listOf()
}