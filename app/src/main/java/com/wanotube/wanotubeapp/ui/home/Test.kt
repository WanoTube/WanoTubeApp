package com.wanotube.wanotubeapp.ui.home

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

internal class Example {
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
    var isAdmin: Boolean? = null

    @SerializedName("user_id")
    @Expose
    var userId: UserId? = null

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

class UserId {
    @SerializedName("_id")
    @Expose
    var id: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("birth_date")
    @Expose
    var birthDate: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

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