package com.wanotube.wanotubeapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wanotube.wanotubeapp.database.dao.AccountDao
import com.wanotube.wanotubeapp.database.dao.CommentDao
import com.wanotube.wanotubeapp.database.dao.VideoDao
import com.wanotube.wanotubeapp.database.entity.DatabaseAccount
import com.wanotube.wanotubeapp.database.entity.DatabaseComment
import com.wanotube.wanotubeapp.database.entity.DatabaseVideo

@Database(entities = [DatabaseVideo::class, DatabaseAccount::class, DatabaseComment::class], version = 5, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract val videoDao: VideoDao
    abstract val accountDao: AccountDao
    abstract val commentDao: CommentDao
}

private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
    val MIGRATION_1_2: Migration = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `DatabaseAccount` (`id` TEXT NOT NULL, `isAdmin` INTEGER NOT NULL, " +
                    "`avatar` TEXT NOT NULL," +
                    "`userId` TEXT NOT NULL," +
                    "`username` TEXT NOT NULL," +
                    "PRIMARY KEY(`id`))")

        }
    }

    val MIGRATION_2_3: Migration = object : Migration(2,3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `DatabaseComment` (`id` TEXT NOT NULL, `content` TEXT NOT NULL, " +
                    "`authorId` TEXT NOT NULL," +
                    "`videoId` TEXT NOT NULL," +
                    "PRIMARY KEY(`id`))")

        }
    }
    val MIGRATION_3_4: Migration = object : Migration(3,4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE DatabaseComment ADD COLUMN authorUsername TEXT")
            database.execSQL("ALTER TABLE DatabaseComment ADD COLUMN authorAvatar TEXT")
        }
    }
    val MIGRATION_4_5: Migration = object : Migration(4,5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE DatabaseVideo ADD COLUMN recognitionResultTitle TEXT")
            database.execSQL("ALTER TABLE DatabaseVideo ADD COLUMN recognitionResultAlbum TEXT")
            database.execSQL("ALTER TABLE DatabaseVideo ADD COLUMN recognitionResultArtist TEXT")
        }
    }
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java,
                    "videos")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
        }
    }
    return INSTANCE
}