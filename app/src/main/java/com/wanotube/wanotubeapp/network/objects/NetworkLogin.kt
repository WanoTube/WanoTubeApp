package com.wanotube.wanotubeapp.network.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResult {
    @SerializedName("token")
    val token: String = ""
    @SerializedName("user")
    val user: LoginUserResult? = null
}

class LoginUserResult {
    @SerializedName("_id")
    var id: Any? = null
    @SerializedName("username")
    var username: String? = null
    @SerializedName("is_admin")
    var isAdmin = false
    @SerializedName("avatar")
    var avatar: String? = null
    @SerializedName("channelId")
    var channelId: Any? = null
    @SerializedName("is_blocked")
    var isBlocked = false
}


class UserResult {
    @SerializedName("user")
    @Expose
    var user: NetworkUser? = null

    @SerializedName("channel_id")
    @Expose
    var channelId: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null
}