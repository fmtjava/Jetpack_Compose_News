package com.fmt.compose.news.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fmt.compose.news.mApp
import com.fmt.compose.news.ui.music.db.Music
import com.fmt.compose.news.ui.music.db.MusicDao
import com.fmt.compose.news.ui.music.work.MusicDatabaseWorker

@Database(entities = [Music::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMusicDao(): MusicDao

    companion object {
        private const val DATABASE_NAME = "music_db"
        val instance: AppDatabase by lazy {
            Room.databaseBuilder(mApp, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        MusicDatabaseWorker.start()
                    }
                }).build()
        }
    }

}