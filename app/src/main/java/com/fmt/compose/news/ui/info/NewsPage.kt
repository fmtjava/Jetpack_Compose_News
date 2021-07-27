package com.fmt.compose.news.ui.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import coil.transform.RoundedCornersTransformation
import com.fmt.compose.news.R
import com.fmt.compose.news.ext.toPx
import com.fmt.compose.news.model.NewsModelModel
import com.fmt.compose.news.model.StoryModel
import com.fmt.compose.news.model.TopStoryModel
import com.fmt.compose.news.view.LoadingPage
import com.fmt.compose.news.view.TitleBar
import com.fmt.compose.news.viewmodel.NewsViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@Composable
fun NewsPage() {
    val viewMode: NewsViewModel = viewModel()
    val state by viewMode.stateLiveData.observeAsState()
    val newsModel by viewMode.newsLiveData.observeAsState(NewsModelModel())
    LoadingPage(state = state!!,
        loadInit = {
            viewMode.getNewsLists()
        }, contentView = {
            Column(Modifier.fillMaxSize()) {
                TitleBar(stringResource(id = R.string.information_title))
                LazyColumn {
                    val stories = newsModel.stories
                    itemsIndexed(stories) { index, item ->
                        if (index == 0) {
                            NewsBanner(newsModel.top_stories)
                        } else {
                            NewsItem(item)
                            if (index != stories.size - 1) {
                                Divider(
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(8.dp, 0.dp)
                                )
                            }
                        }
                    }
                }
            }
        })
}

@ExperimentalPagerApi
@Composable
fun NewsBanner(topStories: List<TopStoryModel>) {
    val context = LocalContext.current
    Box(modifier = Modifier.height(200.dp)) {
        val pagerState = rememberPagerState(
            pageCount = topStories.size,
            infiniteLoop = true
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = rememberCoilPainter(topStories[page].image, fadeIn = true),
                null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        NewsDetailActivity.go(context, topStories[page].title, topStories[page].url)
                    }
            )
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .padding(6.dp)
                .align(Alignment.BottomCenter),
            activeColor = MaterialTheme.colors.primary,
            inactiveColor = Color.White
        )
    }
}

@Composable
fun NewsItem(model: StoryModel) {
    val context = LocalContext.current
    Row(
        Modifier
            .padding(10.dp)
            .clickable {
                NewsDetailActivity.go(context, model.title, model.url)
            }) {
        Image(
            painter = rememberCoilPainter(model.images[0], fadeIn = true, requestBuilder = {
                transformations(RoundedCornersTransformation(2.toPx().toFloat()))
            }),
            null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .width(120.dp)
                .height(80.dp)
        )

        Column(Modifier.weight(1f)) {
            Text(
                model.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp, 3.dp, 0.dp, 0.dp)
            )
            Text(
                model.hint,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(10.dp, 5.dp, 0.dp, 0.dp)
            )
        }
    }
}