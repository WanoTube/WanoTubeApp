package com.wanotube.wanotubeapp.network.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NetworkRecognitionResult {

    @SerializedName("metadata")
    @Expose
    var metadata: Metadata? = null
}

class Metadata {
    @SerializedName("music")
    @Expose
    var music: List<Music>? = null

    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("result_type")
    @Expose
    var resultType: Int? = null
}

class Music {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("album")
    @Expose
    var album: Album? = null

    @SerializedName("artists")
    @Expose
    var artists: List<Artist>? = null
}

class Status {
    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("version")
    @Expose
    var version: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null
}

class Album {
    @SerializedName("name")
    @Expose
    var name: String? = null
}

class Artist {
    @SerializedName("isni")
    @Expose
    var isni: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
}