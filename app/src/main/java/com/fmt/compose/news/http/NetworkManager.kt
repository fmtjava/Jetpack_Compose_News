package com.fmt.compose.news.http

import android.util.Log
import com.fmt.compose.news.BuildConfig
import com.fmt.compose.news.api.Api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ZHIHU_URL = "http://news-at.zhihu.com/api/"

private val TAG = "NetworkManager"

private val okHttpClient by lazy {
    OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor { message -> Log.e(TAG, message) }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        .build()
}

private val retrofit by lazy {
    Retrofit.Builder()
        .client(okHttpClient)
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
