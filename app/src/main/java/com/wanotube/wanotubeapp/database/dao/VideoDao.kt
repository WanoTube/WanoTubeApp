package com.wanotube.wanotubeapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wanotube.wanotubeapp.database.entity.DatabaseVideo

@Dao
interface VideoDao {
    @Query("SELECT * FROM DatabaseVideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    @Query("SELECT * FROM DatabaseVideo WHERE authorId =:userId")
    fun getAllVideoByAuthorId(userId: String): LiveData<List<DatabaseVideo>> //userId not channelId

    @Query("SELECT * FROM DatabaseVideo WHERE authorId =:userId AND visibility = 0")
    fun getAllPublicVideoByAuthorId(userId: String): LiveData<List<DatabaseVideo>> //userId not channelId

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(videos: List<DatabaseVideo>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(video: DatabaseVideo)
}