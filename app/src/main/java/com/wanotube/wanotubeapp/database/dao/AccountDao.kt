package com.wanotube.wanotubeapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wanotube.wanotubeapp.database.entity.DatabaseAccount

@Dao
interface AccountDao {
    @Query("SELECT * FROM DatabaseAccount")
    fun getAccounts(): LiveData<List<DatabaseAccount>>

    @Query("SELECT * FROM DatabaseAccount WHERE id =:id")
    fun getAccount(id: String): LiveData<List<DatabaseAccount>>

    @Insert(onConflict = OnConflictStrategy.IGNORE) //TODO: Check this
    fun insert(account: DatabaseAccount)
}