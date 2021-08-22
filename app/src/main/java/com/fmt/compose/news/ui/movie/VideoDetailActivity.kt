package com.fmt.compose.news.ui.movie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.fmt.compose.news.BaseActivity
import com.fmt.compose.news.R
import com.fmt.compose.news.ext.toPx
import com.fmt.compose.news.ui.picture.BackArrowDown

class VideoDetailActivity : BaseActivity() {

    companion object {
        private const val VIDEO_URL = "video_url"
        private const val VIDEO_TITLE = "video_title"
        fun go(context: Activity, url: String, title: String) {
            Intent(context, VideoDetailActivity::class.java).also {
                it.putExtra(VIDEO_URL, url)
                it.putExtra(VIDEO_TITLE, title)
                context.startActivity(it)
                context.overridePendingTransition(
                    R.anim.slide_bottom_in,
                    0
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(VIDEO_URL)!!
        val title = intent.getStringExtra(VIDEO_TITLE)!!
        setContent {
            VideoViewPage(
                url,
                title
            ) {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }
}

@Composable
fun VideoViewPage(url: String, title: String, click: () -> Unit) {
    val context = LocalContext.current
    Box(
        Modifier
            .fillMaxSize()
            .background(color = Color.Black)) {
        AndroidView(
            factory = {
                val jzvdStd = JzvdStd(context)
                jzvdStd.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200.toPx())
                jzvdStd
            }, Modifier.align(Alignment.Center)
        ) {
            it.setUp(url, title)
            it.startVideoAfterPreloading()
        }
        BackArrowDown(click = click)
    }
}