package com.fmt.compose.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fmt.compose.news.http.ApiService
import com.fmt.compose.news.model.MovieItemModel

class MovieViewModel : BaseViewModel() {

    val moviesLiveData = MutableLiveData<List<MovieItemModel>>()

    fun getMovieLists() {
        launch {
            val movieModel = ApiService.getMovies()
            moviesLiveData.value = movieModel.itemList
        }
    }
}