package com.fmt.compose.news.ui.picture

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.fmt.compose.news.BaseActivity
import com.fmt.compose.news.R

class PhotoActivity : BaseActivity() {

    companion object {
        const val PHOTO_URL = "photo_url"
        fun go(context: Activity, url: String) {
            with(Intent(context, PhotoActivity::class.java)) {
                putExtra(PHOTO_URL, url)
                context.startActivity(this)
                context.overridePendingTransition(
                    R.anim.slide_bottom_in,
                    0
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(PHOTO_URL)!!
        setContent {
            PhotoViewPage(url) {
                finish()
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PhotoViewPage(url: String, click: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        if (scale >= 6.0) scale = 6.0f
        else if (scale <= 1.0) scale = 1.0f

        rotation += rotationChange
    }
    val painter = rememberImagePainter(
        data = url,
        builder = {
            crossfade(true)
        }
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Image(
            painter,
            url,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation
                )
                .transformable(state = state)
                .fillMaxSize()
        )

        if (painter.state is  ImagePainter.State.Loading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        BackArrowDown(click = click)
    }
}

@Composable
fun BackArrowDown(click: () -> Unit) {
    Surface(
        shape = CircleShape,
        modifier = Modifier
            .padding(15.dp, 35.dp, 0.dp, 0.dp)
            .size(24.dp),
        color = Color.Gray
    ) {
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.clickable {
                click()
            })
    }
}