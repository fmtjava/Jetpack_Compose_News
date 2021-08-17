package com.fmt.compose.news

import android.app.Application
import android.content.Context
import com.lzx.starrysky.StarrySky

lateinit var mApp: Context

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mApp = this
        StarrySky.init(this)
            .setNotificationSwitch(true)
            .apply()
    }
}
