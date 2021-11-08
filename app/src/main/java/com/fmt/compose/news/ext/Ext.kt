package com.fmt.compose.news.ext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import com.fmt.compose.news.mApp
import java.text.SimpleDateFormat
import java.util.*

fun Int.toPx(): Int = (this * mApp.resources.displayMetrics.density + 0.5f).toInt()

@SuppressLint("SimpleDateFormat")
fun formatDateMsByMS(milliseconds: Long): String = SimpleDateFormat("mm:ss").format(Date(milliseconds))

fun getScreenWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

fun getScreenHeight(): Int = Resources.getSystem().displayMetrics.heightPixels

fun getStatusBarHeight(): Int {
    var result = 0
    val resourceId =
        mApp.resources?.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId != null && resourceId > 0) {
        result = mApp.resources?.getDimensionPixelSize(resourceId)!!
    }
    return result
}

fun Context.showToast(msg: String?) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()


