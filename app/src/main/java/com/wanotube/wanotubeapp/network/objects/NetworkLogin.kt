package com.wanotube.wanotubeapp.network.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResult {
    @SerializedName("token")
    val token: String = ""
    @SerializedName("user")
    val user: NetworkAccount? = null
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