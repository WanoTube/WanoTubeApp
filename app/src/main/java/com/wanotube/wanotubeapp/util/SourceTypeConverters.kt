package com.wanotube.wanotubeapp.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.wanotube.wanotubeapp.database.entity.DatabaseAccount

class SourceTypeConverters {

    @TypeConverter
    fun accountToString(account: DatabaseAccount): String = Gson().toJson(account)

    @TypeConverter
    fun stringToAccount(string: String): DatabaseAccount = Gson().fromJson(string, DatabaseAccount::class.java)

}