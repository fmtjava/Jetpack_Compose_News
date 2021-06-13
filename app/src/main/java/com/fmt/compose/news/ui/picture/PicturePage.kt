package com.fmt.compose.news.ui.picture

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.transform.CircleCropTransformation
import com.fmt.compose.news.R
import com.fmt.compose.news.view.LoadingPage
import com.fmt.compose.news.viewmodel.PictureViewModel
import com.google.accompanist.coil.rememberCoilPainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PicturePage() {

    val context = LocalContext.current
    val viewModel: PictureViewModel = viewModel()
    val state by viewModel.stateLiveData.observeAsState()
    val picList by viewModel.picLiveData.observeAsState(listOf())

    LoadingPage(
        state = state!!,
        loadInit = { viewModel.getPicList() }) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(id = R.string.pretty_pic_title),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyVerticalGrid(
                cells = GridCells.Fixed(3),
                contentPadding = PaddingValues(5.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                items(picList) { item ->
                    Image(
                        painter = rememberCoilPainter(
                            item.images[0],
                            fadeIn = true,
                            requestBuilder = {
                                transformations(CircleCropTransformation())
                            }),
                        null,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable {
                                PhotoActivity.go(context as Activity, item.images[0])
                            }
                    )
                }
            }
        }
    }
}