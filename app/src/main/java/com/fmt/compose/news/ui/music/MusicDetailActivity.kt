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
import androidx.compose.foundation.clickable
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
import com.fmt.compose.news.ext.showToast
import com.fmt.compose.news.ui.music.db.Music
import com.fmt.compose.news.utils.HiStatusBar
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lzx.starrysky.SongInfo
import com.lzx.starrysky.StarrySky
import com.lzx.starrysky.manager.PlaybackStage
import kotlinx.coroutines.flow.collect
import java.util.*

class MusicDetailActivity : ComponentActivity() {

    lateinit var mMusicList: ArrayList<Music>
    private val mMediaList by lazy { mutableListOf<SongInfo>() }
    private var mPlayItemPosition = 0
    private var mSelectItemPosition by mutableStateOf(0)
    private var mSelectPlayUrl by mutableStateOf("")
    private var mPlayOrPauseIcon by mutableStateOf(R.mipmap.ic_play_btn_pause)

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
        initData()
        initStarrySky()
        initStatusBar()
        setContent {
            Log.e("fmt", "setContent")
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Log.e("fmt", "Box")
                BlurImageView(mSelectPlayUrl)
                DisBackground()
                DisViewPage(musicList = mMusicList, mSelectItemPosition)
                NeedleView()
                OperateView()
            }
        }
    }

    private fun initStatusBar() {
        HiStatusBar.setStatusBar(
            this,
            darkContent = true,
            statusBarColor = Color.TRANSPARENT,
            translucent = true
        )
    }

    private fun initData() {
        mPlayItemPosition = intent.getIntExtra(PLAY_ITEM_POSITION, 0)
        mSelectItemPosition = mPlayItemPosition
        mMusicList = intent.getParcelableArrayListExtra(MUSIC_LIST)!!
        mSelectPlayUrl = mMusicList[mPlayItemPosition].poster
        mMusicList.forEach {
            val songInfo = SongInfo()
            songInfo.songId = it.musicId
            songInfo.songUrl = it.path
            songInfo.songCover = it.poster
            songInfo.songName = it.name
            songInfo.artist = it.author
            mMediaList.add(songInfo)
        }
    }

    private fun initStarrySky() {
        playMusicWithList(mPlayItemPosition)
        StarrySky.with().playbackState().observe(this) {
            when (it.stage) {
                PlaybackStage.PLAYING -> {
                    mPlayOrPauseIcon = R.mipmap.ic_play_btn_pause
                }
                //切歌
                PlaybackStage.SWITCH -> it.songInfo?.let { songInfo ->
                    mSelectPlayUrl = songInfo.songCover
                    mSelectItemPosition = findPositionById(songInfo.songId)
                }
                PlaybackStage.PAUSE,
                PlaybackStage.IDLE -> {
                    mPlayOrPauseIcon = R.mipmap.ic_play_btn_play
                }
                PlaybackStage.ERROR -> {
                    showToast(it.errorMsg)
                }
            }
        }
    }

    private fun findPositionById(songId: String): Int {
        var selectIndex = 0
        mMusicList.forEachIndexed { index, music ->
            if (music.musicId == songId) {
                selectIndex = index
                return@forEachIndexed
            }
        }
        return selectIndex
    }

    private fun playMusicWithList(playItemPosition: Int) {
        StarrySky.with().playMusic(mMediaList, playItemPosition)
    }

    @Composable
    fun BlurImageView(url: String) {
        Log.e("fmt", "BlurImageView")
        val current = LocalContext.current
        AndroidView(factory = {
            val imageView = ImageView(current)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView
        }, Modifier.fillMaxSize()) {
            it.load(url) {
                crossfade(true)
                transformations(BlurTransformation(current, 25f, 3f))
            }
        }
    }

    @Composable
    fun DisBackground() {
        Log.e("fmt", "DisBackground")
        Image(
            painter = painterResource(R.mipmap.ic_disc_blackground),
            contentDescription = null,
            Modifier
                .padding(top = 123.dp)
                .size(274.dp)
        )
    }

    @Composable
    fun NeedleView() {
        Log.e("fmt", "NeedleView")
        Image(
            painter = painterResource(R.mipmap.ic_needle),
            contentDescription = null,
            Modifier.padding(start = 60.dp, top = 45.dp)
        )
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun DisViewPage(musicList: ArrayList<Music>, selectItem: Int) {
        Log.e("fmt", "DisViewPage")
        val pagerState = rememberPagerState(
            pageCount = mMusicList.size,
            initialPage = 0,
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
                        .size(178.dp)
                        .rotate(rotation)
                )
            }
        }
        LaunchedEffect(key1 = pagerState) {
            //通过Flow流监听页面变化
            snapshotFlow {
                pagerState.currentPage
            }.collect { page ->
                mSelectPlayUrl = mMusicList[page].poster
                playMusicWithList(page)
            }
        }
        LaunchedEffect(key1 = selectItem) {
            //默认选中点击进来的选项
            pagerState.animateScrollToPage(
                selectItem,
                animationSpec = tween(easing = LinearOutSlowInEasing)
            )
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
                    Modifier.clickable {
                        StarrySky.with().skipToPrevious()
                    }
                )
                Image(
                    painter = painterResource(mPlayOrPauseIcon),
                    contentDescription = null,
                    Modifier.clickable {
                        if (StarrySky.with().isPlaying()) {
                            StarrySky.with().pauseMusic()
                        } else {
                            StarrySky.with().restoreMusic()
                        }
                    }
                )
                Image(
                    painter = painterResource(R.mipmap.ic_play_btn_next),
                    contentDescription = null,
                    Modifier.clickable {
                        StarrySky.with().skipToNext()
                    }
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
