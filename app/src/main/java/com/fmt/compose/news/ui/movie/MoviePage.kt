package com.fmt.compose.news.ui.movie

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
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
import com.fmt.compose.news.R
import com.fmt.compose.news.ext.formatDateMsByMS
import com.fmt.compose.news.model.MovieItemModel
import com.fmt.compose.news.ui.theme.BgColor
import com.fmt.compose.news.view.LoadingPage
import com.fmt.compose.news.viewmodel.MovieViewModel
import com.google.accompanist.coil.rememberCoilPainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviePage() {

    val viewModel: MovieViewModel = viewModel()
    val state by viewModel.stateLiveData.observeAsState()
    val movieList by viewModel.moviesLiveData.observeAsState(listOf())

    LoadingPage(
        state = state!!,
        loadInit = { viewModel.getMovieLists() }) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(id = R.string.recommend_movie_title),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            LazyVerticalGrid(
                cells = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgColor),
                contentPadding = PaddingValues(5.dp)
            ) {
                items(movieList) { item ->
                    MovieItem(item)
                }
            }
        }
    }
}

@Composable
fun MovieItem(model: MovieItemModel) {
    val context = LocalContext.current
    Card(Modifier.padding(4.dp)) {
        Column(Modifier.clickable {
            VideoDetailActivity.go(context as Activity, model.data.playUrl, model.data.title)
        }) {
            Box {
                Image(
                    painter = rememberCoilPainter(
                        model.data.cover.feed,
                        fadeIn = true,
                    ),
                    null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )
                Text(
                    formatDateMsByMS((model.data.duration * 1000).toLong()),
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.BottomEnd)
                )
            }
            Text(
                text = model.data.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp, 10.dp, 0.dp, 3.dp)
            )
            Text(
                model.data.category,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(10.dp, 2.dp, 0.dp, 10.dp)
            )
        }
    }
}