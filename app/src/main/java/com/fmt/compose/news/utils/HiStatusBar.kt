package com.fmt.compose.news.utils

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.WindowManager

object HiStatusBar {
    fun setStatusBar(
        activity: Activity,
        darkContent: Boolean,
        statusBarColor: Int = Color.WHITE,
        translucent: Boolean = false
    ) {
        val window = activity.window
        val decorView = window.decorView
        var visibility = decorView.systemUiVisibility
        //请求系统绘制系统状态栏,但是不能和下面的flag同时出现
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //设置状态栏颜色，但必须搭配以上两个属性
        window.statusBarColor = statusBarColor
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            visibility = if (darkContent) {
                //白底黑字
                visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                //黑底白字
                visibility and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

        if (translucent) {
            visibility = visibility or
                    //使得页面全屏,但是信号、时间等字体看不见了，注意不是 `View.SYSTEM_UI_FLAG_FULLSCREEN`
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    //添加该flag,恢复信号、时间等字体可见
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        decorView.systemUiVisibility = visibility
    }
}