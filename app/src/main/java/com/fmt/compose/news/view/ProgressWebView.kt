package com.fmt.compose.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.fmt.compose.news.R
import com.fmt.compose.news.ext.toPx

class ProgressWebView(context: Context, attrs: AttributeSet? = null) :
    WebView(context, attrs) {

    private val mProgressBar: ProgressBar =
        ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)

    init {
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4.toPx())
        mProgressBar.layoutParams = layoutParams
        val drawable = ContextCompat.getDrawable(context, R.drawable.web_progress_bar_states)
        mProgressBar.progressDrawable = drawable
        addView(mProgressBar)
        webChromeClient = WebChromeClient()
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress == 100) {
                mProgressBar.visibility = GONE
            } else {
                if (mProgressBar.visibility == GONE) mProgressBar.visibility = VISIBLE
                mProgressBar.progress = newProgress
            }
            super.onProgressChanged(view, newProgress)
        }
    }
}