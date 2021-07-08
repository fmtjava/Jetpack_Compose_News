package com.fmt.compose.news

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fmt.compose.news.ui.theme.Purple500
import com.fmt.compose.news.utils.SpUtils
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

class GuideActivity : ComponentActivity() {

    companion object {
        const val HAS_SHOW_GUIDE = "has_show_guide"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SpUtils.getBoolean(HAS_SHOW_GUIDE)) {
            go2Main()
        } else {
            setContent {
                GuidePage(listOf(R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3)) {
                    SpUtils.setBoolean(HAS_SHOW_GUIDE, true)
                    go2Main()
                }
            }
        }
    }

    private fun go2Main() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GuidePage(images: List<Int>, go2Main: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(
            pageCount = images.size,
            initialOffscreenLimit = 2
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize(),
        ) { page ->
            Image(
                painter = painterResource(images[page]),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
        if (pagerState.currentPage == images.size - 1) {
            Button(
                onClick = {
                    go2Main()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Purple500),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp),
            ) {
                Text(
                    stringResource(R.string.start_experience),
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp),
            activeColor = Purple500,
            inactiveColor = Color.White
        )
    }
}