package com.fmt.compose.news.ext

import android.content.res.Resources
import com.fmt.compose.news.mApp
import java.text.SimpleDateFormat
import java.util.*

fun Int.toPx(): Int {
    val scale = mApp.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun formatDateMsByMS(milliseconds: Long): String {
    val simpleDateFormat = SimpleDateFormat("mm:ss")
    return simpleDateFormat.format(Date(milliseconds))
}

fun getScreenWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

fun getScreenHeight(): Int = Resources.getSystem().displayMetrics.heightPixels


