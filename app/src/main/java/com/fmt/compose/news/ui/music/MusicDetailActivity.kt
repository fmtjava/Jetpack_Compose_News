package com.fmt.compose.news.ui.music

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import coil.load
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import com.fmt.compose.news.R
import com.fmt.compose.news.ui.music.db.Music
import com.fmt.compose.news.utils.HiStatusBar
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lzx.starrysky.SongInfo
import com.lzx.starrysky.StarrySky
import kotlinx.coroutines.flow.collect
import java.util.*

class MusicDetailActivity : ComponentActivity() {

    lateinit var mMusicList: ArrayList<Music>
    private var mPlayItemPosition = 0
    private var mSelectState by mutableStateOf(mPlayItemPosition)

    companion object {
        private const val MUSIC_LIST = "music_list"
        private const val PLAY_ITEM_POSITION = "play_item_position"

        fun go(context: Activity, musicList: ArrayList<Music>, playItemPosition: Int) {
            with(Intent(context, MusicDetailActivity::class.java)) {
                putParcelableArrayListExtra(MUSIC_LIST, musicList)
                putExtra(PLAY_ITEM_POSITION, playItemPosition)
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
        mMusicList = intent.getParcelableArrayListExtra(MUSIC_LIST)!!
        mPlayItemPosition = intent.getIntExtra(PLAY_ITEM_POSITION, 0)
        mSelectState = mPlayItemPosition
        HiStatusBar.setStatusBar(
            this,
            darkContent = true,
            statusBarColor = Color.TRANSPARENT,
            translucent = true
        )
        val info = SongInfo()
        info.songId = mMusicList[mPlayItemPosition].musicId
        info.songUrl = mMusicList[mPlayItemPosition].path
        StarrySky.with().playMusicByInfo(info)
        setContent {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                BlurImageView(mMusicList[mSelectState].poster, Modifier.fillMaxSize())
                Image(
                    painter = painterResource(R.mipmap.ic_disc_blackground),
                    contentDescription = null,
                    Modifier
                        .padding(top = 123.dp)
                        .size(274.dp)
                )
                DisViewPage(musicList = mMusicList, mPlayItemPosition)
                Image(
                    painter = painterResource(R.mipmap.ic_needle),
                    contentDescription = null,
                    Modifier.padding(start = 60.dp, top = 45.dp)
                )
                OperateView()
            }
        }
    }

    @Composable
    fun BlurImageView(url: String, modifier: Modifier = Modifier) {
        val current = LocalContext.current
        AndroidView(factory = {
            val imageView = ImageView(current)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView
        }, modifier) {
            it.load(url) {
                crossfade(true)
                transformations(BlurTransformation(current, 25f, 3f))
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun DisViewPage(musicList: ArrayList<Music>, selectItem: Int) {
        val pagerState = rememberPagerState(
            pageCount = musicList.size,
            initialOffscreenLimit = 2,
            infiniteLoop = true
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
        ) { page ->
            val infiniteTransition = rememberInfiniteTransition()
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 30000,
                        easing = LinearEasing
                    )
                )
            )
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.mipmap.ic_disc),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .padding(top = 59.dp)
                        .size(402.dp)
                )
                Image(
                    painter = rememberImagePainter(
                        data = musicList[page].poster,
                        builder = {
                            crossfade(true)
                            transformations(CircleCropTransformation())
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 60.dp)
                        .size(178.dp).rotate(rotation)
                )
            }
        }
        LaunchedEffect(key1 = pagerState) {
            //默认选中点击进来的选项
            pagerState.scrollToPage(selectItem)
            //通过Flow流监听页面变化
            snapshotFlow {
                pagerState.currentPage
            }.collect { page ->
                mSelectState = page
            }
        }
    }


    @Composable
    fun OperateView() {
        Box(
            Modifier
                .fillMaxHeight()
                .padding(20.dp), contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.mipmap.ic_play_btn_prev),
                    contentDescription = null,
                )
                Image(
                    painter = painterResource(R.mipmap.ic_play_btn_play),
                    contentDescription = null,
                )
                Image(
                    painter = painterResource(R.mipmap.ic_play_btn_next),
                    contentDescription = null,
                )
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            0,
            R.anim.slide_bottom_out
        )
    }
}
