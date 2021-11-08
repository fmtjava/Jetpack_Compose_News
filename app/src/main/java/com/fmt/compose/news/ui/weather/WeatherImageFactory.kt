package com.fmt.compose.news.ui.weather

import com.fmt.compose.news.R

object WeatherImageFactory {

    fun getWeatherImage(weather: String): Int =
        if (weather == "多云" || weather == "多云转阴" || weather == "多云转晴") {
            R.mipmap.biz_plugin_weather_duoyun
        } else if (weather == "中雨" || weather == "中到大雨") {
            R.mipmap.biz_plugin_weather_zhongyu
        } else if (weather == "雷阵雨") {
            R.mipmap.biz_plugin_weather_leizhenyu
        } else if (weather == "阵雨" || weather == "阵雨转多云") {
            R.mipmap.biz_plugin_weather_zhenyu
        } else if (weather == "暴雪") {
            R.mipmap.biz_plugin_weather_baoxue
        } else if (weather == "暴雨") {
            R.mipmap.biz_plugin_weather_baoyu
        } else if (weather == "大暴雨") {
            R.mipmap.biz_plugin_weather_dabaoyu
        } else if (weather == "大雪") {
            R.mipmap.biz_plugin_weather_daxue
        } else if (weather == "大雨" || weather == "大雨转中雨") {
            R.mipmap.biz_plugin_weather_dayu
        } else if (weather == "雷阵雨冰雹") {
            R.mipmap.biz_plugin_weather_leizhenyubingbao
        } else if (weather == "晴") {
            R.mipmap.biz_plugin_weather_qing
        } else if (weather == "沙尘暴") {
            R.mipmap.biz_plugin_weather_shachenbao
        } else if (weather == "特大暴雨") {
            R.mipmap.biz_plugin_weather_tedabaoyu
        } else if (weather == "雾" || weather == "雾霾") {
            R.mipmap.biz_plugin_weather_wu
        } else if (weather == "小雪") {
            R.mipmap.biz_plugin_weather_xiaoxue
        } else if (weather == "小雨") {
            R.mipmap.biz_plugin_weather_xiaoyu
        } else if (weather == "阴") {
            R.mipmap.biz_plugin_weather_yin
        } else if (weather == "雨夹雪") {
            R.mipmap.biz_plugin_weather_yujiaxue
        } else if (weather == "阵雪") {
            R.mipmap.biz_plugin_weather_zhenxue
        } else if (weather == "中雪") {
            R.mipmap.biz_plugin_weather_zhongxue
        } else {
            R.mipmap.biz_plugin_weather_duoyun
        }
}