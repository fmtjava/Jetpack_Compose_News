package com.fmt.compose.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fmt.compose.news.http.ApiService
import com.fmt.compose.news.model.PictureModel

class PictureViewModel : BaseViewModel() {

    val picLiveData = MutableLiveData<List<PictureModel>>()

    fun getPicList() {
        launch {
            val pics = ApiService.getPics().data
            picLiveData.value = pics
        }
    }

}