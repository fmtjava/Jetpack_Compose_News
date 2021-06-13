package com.fmt.compose.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fmt.compose.news.http.ApiService
import com.fmt.compose.news.model.NewsModelModel
import com.fmt.compose.news.model.StoryModel

class NewsViewModel : BaseViewModel() {

    val newsLiveData = MutableLiveData<NewsModelModel>()

    fun getNewsLists() {
        launch {
            val newsModel = ApiService.getNews()
            //Banner占位
            newsModel.stories.add(0, StoryModel())
            newsLiveData.value = newsModel
        }
    }
}