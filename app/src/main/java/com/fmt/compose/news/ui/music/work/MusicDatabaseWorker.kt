package com.fmt.compose.news.ui.music.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.fmt.compose.news.db.AppDatabase
import com.fmt.compose.news.mApp
import com.fmt.compose.news.ui.music.db.Music
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicDatabaseWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val MUSIC_DATA_FILE_NAME = "MusicSource.json"

    companion object {
        fun start() {
            val request = OneTimeWorkRequestBuilder<MusicDatabaseWorker>().build()
            WorkManager.getInstance(mApp).enqueue(request)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        applicationContext.assets.open(MUSIC_DATA_FILE_NAME).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val musicListType = object : TypeToken<List<Music>>() {}.type
                val musicList: List<Music> = Gson().fromJson(jsonReader, musicListType)
                AppDatabase.instance.getMusicDao().insertAll(musicList)
                Result.success()
            }
        }
    }
}