package com.fmt.compose.news.ui.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fmt.compose.news.R
import com.fmt.compose.news.model.WeatherModel
import com.fmt.compose.news.view.LoadingPage
import com.fmt.compose.news.view.TitleBar
import com.fmt.compose.news.viewmodel.WeatherViewModel

@Composable
fun WeatherPage() {
    val viewModel: WeatherViewModel = viewModel()
    val state by viewModel.stateLiveData.observeAsState()
    val weatherList by viewModel.weatherLiveData.observeAsState(listOf())

    LoadingPage(
        state = state!!,
        loadInit = { viewModel.getWeathers() }) {
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.mipmap.biz_news_local_weather_bg_big),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
            )
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleBar(text = stringResource(id = R.string.actual_weather_title))

                if (!weatherList.isNullOrEmpty()) {
                    val currentWeather = weatherList[0]
                    Row(Modifier.padding(0.dp, 40.dp, 0.dp, 20.dp)) {
                        Text(text = currentWeather.citynm, color = Color.White)
                        Text(
                            text = "${currentWeather.days} ${currentWeather.week}",
                            color = Color.White,
                            modifier = Modifier.padding(24.dp, 0.dp)
                        )
                    }
                    Row {
                        Image(
                            painter = painterResource(currentWeather.imgRes),
                            null,
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Column(Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)) {
                            Text(
                                text = currentWeather.temperature,
                                color = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                            Text(
                                text = currentWeather.weather,
                                color = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                            Text(
                                text = currentWeather.wind,
                                color = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                    LazyRow(Modifier.padding(0.dp, 60.dp, 0.dp, 0.dp)) {
                        items(weatherList.subList(1, weatherList.size)) {
                            WeatherItem(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherItem(model: WeatherModel) {
    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = model.week,
            color = Color.White,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 12.dp)
        )
        Image(
            painter = painterResource(model.imgRes),
            null,
            colorFilter = ColorFilter.tint(Color.White)
        )
        Text(
            text = model.temperature,
            color = Color.White,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = model.weather,
            color = Color.White,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = model.wind,
            color = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}