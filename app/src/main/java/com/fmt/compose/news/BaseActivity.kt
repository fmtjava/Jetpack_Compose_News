package com.fmt.compose.news

import androidx.activity.ComponentActivity

open class BaseActivity : ComponentActivity() {

    override fun finish() {
        super.finish()
        overridePendingTransition(
            0,
            R.anim.slide_bottom_out
        )
    }
}