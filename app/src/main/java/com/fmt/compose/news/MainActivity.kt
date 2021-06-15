package com.fmt.compose.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
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
import com.fmt.compose.news.ui.picture.PicturePage
import com.fmt.compose.news.ui.theme.Jetpack_Compose_newsTheme
import com.fmt.compose.news.ui.theme.Purple500
import com.fmt.compose.news.ui.weather.WeatherPage
import com.fmt.compose.news.viewmodel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = viewModel()
            val selectedIndex by viewModel.getSelectedIndex().observeAsState(0)
            Jetpack_Compose_newsTheme {
                Column {
                    val pagerState = rememberPagerState(
                        pageCount = 4,
                        initialPage = selectedIndex,
                        initialOffscreenLimit = 3
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
                            3 -> WeatherPage()
                        }
                    }
                    BottomNavigationAlwaysShowLabelComponent(pagerState)
                }
            }
        }
    }
}

val listItems = listOf(
    mApp.getString(R.string.news_tab_title),
    mApp.getString(R.string.video_tab_title),
    mApp.getString(R.string.pic_tab_title),
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
                        3 -> BottomIcon(Icons.Filled.Place, selectedIndex, index)
                    }
                },
                label = {
                    Text(label, color = if (selectedIndex == index) Purple500 else Color.Gray)
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
        tint = if (selectedIndex == index) Purple500 else Color.Gray
    )
}
