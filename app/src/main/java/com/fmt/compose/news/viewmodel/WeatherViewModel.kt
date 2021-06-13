package com.fmt.compose.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fmt.compose.news.http.ApiService
import com.fmt.compose.news.model.WeatherModel
import com.fmt.compose.news.ui.weather.WeatherImageFactory

class WeatherViewModel : BaseViewModel() {

    val weatherLiveData = MutableLiveData<List<WeatherModel>>()

    fun getWeathers() {
        launch {
            val weatherResponse = ApiService.getWeathers()
            weatherResponse.result.forEach {
                it.imgRes = WeatherImageFactory.getWeatherImage(it.weather)
            }
            weatherLiveData.value = weatherResponse.result
        }
    }

}