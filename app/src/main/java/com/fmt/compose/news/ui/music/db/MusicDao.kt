package com.fmt.compose.news.ui.music.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MusicDao {

    @Query("select * from music")
    fun getMusicList(): LiveData<List<Music>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(musicList: List<Music>)

}