package com.wanotube.wanotubeapp.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch a wanotube playlist.
 */
interface WanoTubeService {
    @GET("wanotube")
    suspend fun getPlaylist(): NetworkVideoContainer
}

/**
 * Main entry point for network access. Call like `WanoTubeNetwork.wanotubes.getPlaylist()`
 */
object WanoTubeNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("BASEURL")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val wanotubes = retrofit.create(WanoTubeService::class.java)

}
