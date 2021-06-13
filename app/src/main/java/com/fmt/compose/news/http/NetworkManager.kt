package com.fmt.compose.news.http

import com.fmt.compose.news.api.Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ZHIHU_URL = "http://news-at.zhihu.com/api/"
private const val BASE_URL = "https://api.apiopen.top/"

private val retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(
            ZHIHU_URL
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

object ApiService : Api by retrofit.create(
    Api::
    class.java
)
