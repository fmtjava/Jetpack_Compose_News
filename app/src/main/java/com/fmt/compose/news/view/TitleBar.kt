package com.fmt.compose.news.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleBar(text: String, showBack: Boolean = false, backClick: (() -> Unit)? = null) {
    TopAppBar(contentPadding = PaddingValues.Absolute()) {
        if (showBack) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxHeight()
                    .clickable {
                        backClick?.invoke()
                    }

            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    null,
                    tint = Color.White,
                    modifier = Modifier.padding(15.dp, 0.dp)
                )
            }
        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}