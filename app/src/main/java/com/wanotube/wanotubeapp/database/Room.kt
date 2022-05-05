package com.wanotube.wanotubeapp.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Dao
interface VideoDao {
    @Query("SELECT * FROM DatabaseVideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(videos: List<DatabaseVideo>)
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM DatabaseAccount")
    fun getAccounts(): LiveData<List<DatabaseAccount>>
}

@Database(entities = [DatabaseVideo::class, DatabaseAccount::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract val videoDao: VideoDao
    abstract val accountDao: AccountDao
}

private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
//    val MIGRATION_1_2: Migration = object : Migration(1,2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            // Since we didn't alter the table, there's nothing else to do here.
//        }
//    }
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java,
                    "videos")
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
    return INSTANCE
}