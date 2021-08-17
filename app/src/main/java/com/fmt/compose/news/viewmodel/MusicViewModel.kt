package com.fmt.compose.news.viewmodel

import androidx.lifecycle.LiveData
import com.fmt.compose.news.db.AppDatabase
import com.fmt.compose.news.ui.music.db.Music

class MusicViewModel : BaseViewModel() {

    fun getMusicList():LiveData<List<Music>> = AppDatabase.instance.getMusicDao().getMusicList()

}