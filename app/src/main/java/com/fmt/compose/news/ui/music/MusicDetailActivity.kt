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
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import coil.load
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import com.fmt.compose.news.R
import com.fmt.compose.news.ext.showToast
import com.fmt.compose.news.ui.music.db.Music
import com.fmt.compose.news.ui.theme.BgColor
import com.fmt.compose.news.utils.HiStatusBar
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lzx.starrysky.OnPlayProgressListener
import com.lzx.starrysky.SongInfo
import com.lzx.starrysky.StarrySky
import com.lzx.starrysky.manager.PlaybackStage
import com.lzx.starrysky.utils.formatTime
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class MusicDetailActivity : ComponentActivity() {

    private lateinit var mMusicList: ArrayList<Music>
    private val mMediaList by lazy { mutableListOf<SongInfo>() }
    private var mPlayItemPosition = 0
    private var mSelectItemPosition by mutableStateOf(0)
    private var mSelectPlayUrl by mutableStateOf("")
    private var mPlayOrPauseIcon by mutableStateOf(R.mipmap.ic_play_btn_pause)
    private var mProgress by mutableStateOf(0f)
    private var mCurrTimeText by mutableStateOf("")
    private var mTotalTimeText by mutableStateOf("")
    private var mIsPause by mutableStateOf(false)
    private var mCurrentMusicTotalTime = 0L

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
                SingerInfoView()
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
        //监听播放状态
        StarrySky.with().playbackState().observe(this) {
            when (it.stage) {
                PlaybackStage.PLAYING -> {
                    mIsPause = false
                    mPlayOrPauseIcon = R.mipmap.ic_play_btn_pause
                }
                //切歌
                PlaybackStage.SWITCH -> it.songInfo?.let { songInfo ->
                    mSelectPlayUrl = songInfo.songCover
                    mSelectItemPosition = findPositionById(songInfo.songId)
                }
                PlaybackStage.PAUSE,
                PlaybackStage.IDLE -> {
                    mIsPause = true
                    mPlayOrPauseIcon = R.mipmap.ic_play_btn_play
                }
                PlaybackStage.ERROR -> {
                    showToast(it.errorMsg)
                }
            }
        }
        //监听播放进度
        StarrySky.with().setOnPlayProgressListener(object : OnPlayProgressListener {
            override fun onPlayProgress(currPos: Long, duration: Long) {
                if (duration > 0) {
                    //计算当前的播放进度
                    mProgress =
                        ((currPos * 100 / duration).toFloat().roundToInt() / 100.0).toFloat()
                    //计算当前时间
                    mCurrTimeText = currPos.formatTime()
                    //计算总时间
                    mTotalTimeText = duration.formatTime()
                    mCurrentMusicTotalTime = duration
                }
            }
        })
    }

    //查询播放下标
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

    //播放列表指定位置的音乐
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
                .padding(top = 183.dp)
                .size(274.dp)
        )
    }

    @Composable
    private fun SingerInfoView() {
        Column(Modifier.padding(top = 45.dp)) {
            Text(
                mMusicList[mPlayItemPosition].author,
                style = TextStyle(color = White, fontSize = 14.sp)
            )
            Text(
                mMusicList[mPlayItemPosition].name,
                style = TextStyle(color = BgColor, fontSize = 12.sp)
            )
        }
    }

    @Composable
    fun NeedleView() {
        Log.e("fmt", "NeedleView")
        Image(
            painter = painterResource(R.mipmap.ic_needle),
            contentDescription = null,
            Modifier
                .padding(start = 60.dp, top = 100.dp)
                .size(138.dp)
        )
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun DisViewPage(musicList: ArrayList<Music>, selectItem: Int) {
        Log.e("fmt", "DisViewPage")
        val pagerState = rememberPagerState(
            pageCount = mMusicList.size,
            initialPage = selectItem,
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
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
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
                        .padding(top = 119.dp)
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
                        .padding(top = 120.dp)
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
        Log.e("fmt", "OperateView")
        Column(
            Modifier
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(mCurrTimeText, style = TextStyle(color = White, fontSize = 10.sp))
                Slider(
                    value = mProgress,
                    onValueChange = { pos ->
                        mProgress = pos
                        Log.e("fmt", "pos=$pos")
                        StarrySky.with().seekTo((pos * mCurrentMusicTotalTime).toLong(), true)
                    },
                    Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = White,
                        inactiveTickColor = BgColor,
                        activeTrackColor = White
                    )
                )
                Text(mTotalTimeText, style = TextStyle(color = White, fontSize = 10.sp))
            }
            Box(contentAlignment = Alignment.Center) {
                Log.e("fmt", "Bootom BOX")
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
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            0,
            R.anim.slide_bottom_out
        )
    }
}
