package com.wanotube.wanotubeapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wanotube.wanotubeapp.database.entity.DatabaseComment

@Dao
interface CommentDao {
    @Query("SELECT * FROM DatabaseComment")
    fun getComments(): LiveData<List<DatabaseComment>>

    @Insert(onConflict = OnConflictStrategy.IGNORE) //TODO: Check this
    fun insert(comment: DatabaseComment)
}