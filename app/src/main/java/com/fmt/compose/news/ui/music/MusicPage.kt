package com.fmt.compose.news.ui.music

import android.app.Activity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.fmt.compose.news.R
import com.fmt.compose.news.ext.toPx
import com.fmt.compose.news.ui.music.db.Music
import com.fmt.compose.news.ui.theme.InfoColor
import com.fmt.compose.news.ui.theme.TitleColor
import com.fmt.compose.news.view.TitleBar
import com.fmt.compose.news.viewmodel.MusicViewModel

@Composable
fun MusicPage(viewModel: MusicViewModel = viewModel()) {

    val musicList by viewModel.getMusicList().observeAsState(arrayListOf())

    Column(
        Modifier
            .fillMaxSize()
    ) {
        TitleBar(stringResource(R.string.recommend_music_title))
        LazyColumn {
            itemsIndexed(musicList) { index, item ->
                MusicItem(item, index, musicList as ArrayList<Music>)
                if (index != musicList.size - 1) {
                    Divider()
                }
            }
        }
    }
}

@Composable
fun MusicItem(item: Music, index: Int, musicList: ArrayList<Music>) {
    val current = LocalContext.current
    Row(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                MusicDetailActivity.go(current as Activity, musicList, index)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(
                data = item.poster,
                builder = {
                    crossfade(true)
                    transformations(RoundedCornersTransformation(2.toPx().toFloat()))
                }),
            null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .width(56.dp)
                .height(56.dp)
        )
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 16.dp)
        ) {
            Text(
                item.name,
                style = TextStyle(
                    color = TitleColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                item.author,
                Modifier.padding(top = 5.dp),
                style = TextStyle(
                    color = InfoColor,
                    fontSize = 14.sp,
                )
            )
        }
    }
}

