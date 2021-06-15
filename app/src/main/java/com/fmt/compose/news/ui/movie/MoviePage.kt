package com.fmt.compose.news.ui.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fmt.compose.news.R
import com.fmt.compose.news.api.Api
import com.fmt.compose.news.mApp
import com.fmt.compose.news.model.MovieTabModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

private val movieTabList = listOf(
    MovieTabModel(
        mApp.getString(R.string.weekly_recommend_title),
        Api.WEEK_MOVIE_URL
    ),
    MovieTabModel(mApp.getString(R.string.monthly_recommend_title), Api.MONTH_MOVIE_URL),
    MovieTabModel(mApp.getString(R.string.monthly_recommend_title), Api.HISTORICAL_MOVIE_URL)
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MoviePage() {
    Column(Modifier.fillMaxSize()) {
        TopAppBar {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.recommend_movie_title),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        val pagerState =
            rememberPagerState(pageCount = movieTabList.size, initialOffscreenLimit = 2)
        val scope = rememberCoroutineScope()

        TabRow(
            selectedTabIndex = pagerState.currentPage,
        ) {
            movieTabList.forEachIndexed { index, model ->
                Tab(
                    text = { Text(model.tabName) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->
            MovieTabPage(movieTabList[page].apiUrl)
        }
    }
}