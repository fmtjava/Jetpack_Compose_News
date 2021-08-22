package com.fmt.compose.news

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fmt.compose.news.ui.info.NewsPage
import com.fmt.compose.news.ui.movie.MoviePage
import com.fmt.compose.news.ui.music.MusicPage
import com.fmt.compose.news.ui.picture.PicturePage
import com.fmt.compose.news.ui.theme.Jetpack_Compose_newsTheme
import com.fmt.compose.news.ui.weather.WeatherPage
import com.fmt.compose.news.viewmodel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @ExperimentalFoundationApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = viewModel()
            val selectedIndex by viewModel.getSelectedIndex().observeAsState(0)
            Jetpack_Compose_newsTheme {
                Column {
                    /*val pagerState = rememberPagerState(
                        pageCount = 5,
                        initialPage = selectedIndex,
                        initialOffscreenLimit = 4
                    )
                    HorizontalPager(
                        state = pagerState,
                        dragEnabled = false,
                        modifier = Modifier.weight(1f)
                    ) { page ->
                        when (page) {
                            0 -> NewsPage()
                            1 -> MoviePage()
                            2 -> PicturePage()
                            3 -> MusicPage()
                            4 -> WeatherPage()
                        }
                    }
                    BottomNavigationAlwaysShowLabelComponent(pagerState)*/
                    MusicPage()
                }
            }
        }
    }
}

val listItems = listOf(
    mApp.getString(R.string.news_tab_title),
    mApp.getString(R.string.video_tab_title),
    mApp.getString(R.string.pic_tab_title),
    mApp.getString(R.string.music_tab_title),
    mApp.getString(R.string.weather_tab_title)
)

@ExperimentalPagerApi
@Composable
fun BottomNavigationAlwaysShowLabelComponent(pagerState: PagerState) {
    val viewModel: MainViewModel = viewModel()
    val selectedIndex by viewModel.getSelectedIndex().observeAsState(0)
    val scope = rememberCoroutineScope()

    BottomNavigation(backgroundColor = Color.White) {
        listItems.forEachIndexed { index, label ->
            BottomNavigationItem(
                icon = {
                    when (index) {
                        0 -> BottomIcon(Icons.Filled.Home, selectedIndex, index)
                        1 -> BottomIcon(Icons.Filled.List, selectedIndex, index)
                        2 -> BottomIcon(Icons.Filled.Favorite, selectedIndex, index)
                        3 -> BottomIcon(Icons.Filled.ThumbUp, selectedIndex, index)
                        4 -> BottomIcon(Icons.Filled.Place, selectedIndex, index)
                    }
                },
                label = {
                    Text(
                        label,
                        color = if (selectedIndex == index) MaterialTheme.colors.primary else Color.Gray
                    )
                },
                selected = selectedIndex == index,
                onClick = {
                    viewModel.saveSelectIndex(index)
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
                })
        }
    }
}

@Composable
private fun BottomIcon(imageVector: ImageVector, selectedIndex: Int, index: Int) {
    Icon(
        imageVector,
        null,
        tint = if (selectedIndex == index) MaterialTheme.colors.primary else Color.Gray
    )
}
