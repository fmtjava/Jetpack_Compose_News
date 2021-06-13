package com.fmt.compose.news.utils

import android.content.Context
import android.content.SharedPreferences
import com.fmt.compose.news.mApp

object SpUtils {

    private const val SP_FILE_NAME = "sp_news_file"

    private val prefs: SharedPreferences by lazy {
        mApp.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun getBoolean(name: String): Boolean = prefs.getBoolean(name, false)

    fun setBoolean(name: String, value: Boolean) {
        with(prefs.edit()) {
            putBoolean(name, value)
            apply()
        }
    }

}