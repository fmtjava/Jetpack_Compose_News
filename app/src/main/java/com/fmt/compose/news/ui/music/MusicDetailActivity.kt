package com.fmt.compose.news.ui.music

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import coil.compose.rememberImagePainter
import coil.load
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import com.fmt.compose.news.BaseActivity
import com.fmt.compose.news.R
import com.fmt.compose.news.ui.music.db.Music
import com.fmt.compose.news.ui.theme.BgColor
import com.fmt.compose.news.viewmodel.MusicDetailViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collect
import java.util.*

class MusicDetailActivity : BaseActivity() {

    companion object {
        const val MUSIC_LIST = "music_list"
        const val PLAY_ITEM_POSITION = "play_item_position"

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

    private val mViewMode: MusicDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initStatusBar()
        setContent {
            rememberSystemUiController().setStatusBarColor(Transparent)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                BlurImageView(mViewMode.mSelectPlayUrl)
                DisBackground()
                DisViewPage(musicList = mViewMode.mMusicList, mViewMode.mSelectItemPosition)
                SingerInfoView {
                    finish()
                }
                NeedleView()
                OperateView()
            }
        }
    }

    private fun initStatusBar() {
       WindowCompat.setDecorFitsSystemWindows(window,false)
    }

    private fun initData() {
        mViewMode.init(intent, this)
    }

    @Composable
    fun BlurImageView(url: String) {
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
                allowRgb565(true)
                bitmapConfig(Bitmap.Config.RGB_565)
                lifecycle(this@MusicDetailActivity)
                transformations(BlurTransformation(current, 25f, 3f))
            }
        }
    }

    //高斯模糊背景控件
    @Composable
    fun DisBackground() {
        Image(
            painter = painterResource(R.mipmap.ic_disc_blackground),
            contentDescription = null,
            Modifier
                .padding(top = 183.dp)
                .size(274.dp)
        )
    }

    //唱片信息控件
    @Composable
    private fun SingerInfoView(backClick: (() -> Unit)? = null) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 45.dp)
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = White,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(40.dp)
                    .clickable {
                        backClick?.invoke()
                    }
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    mViewMode.mMusicList[mViewMode.mPlayItemPosition].author,
                    style = TextStyle(color = White, fontSize = 14.sp)
                )
                Text(
                    mViewMode.mMusicList[mViewMode.mPlayItemPosition].name,
                    style = TextStyle(color = BgColor, fontSize = 12.sp)
                )
            }
        }
    }

    //唱针控件
    @Composable
    fun NeedleView() {
        val degrees by animateFloatAsState(targetValue = if (mViewMode.mAnimationState == MusicDetailViewModel.RUNNING) 0f else -30f)
        Image(
            painter = painterResource(R.mipmap.ic_needle),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 60.dp, top = 100.dp)
                .size(138.dp)
                .graphicsLayer(transformOrigin = TransformOrigin(0.25f, 0.1f), rotationZ = degrees)
        )
    }

    //唱片控件
    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun DisViewPage(musicList: ArrayList<Music>, selectItem: Int) {
        val pagerState = rememberPagerState(
            pageCount = mViewMode.mMusicList.size,
            initialPage = selectItem,
            initialOffscreenLimit = 2,
            infiniteLoop = true
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
        ) { page ->
            //初始化唱盘转动动画
            val animation = remember {
                TargetBasedAnimation(
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 20000,
                            easing = LinearEasing
                        )
                    ),
                    typeConverter = Float.VectorConverter,
                    initialValue = 0f,
                    targetValue = 360f
                )
            }
            //记录上一次的播放时间
            var playTime by remember { mutableStateOf(0L) }
            //记录动画改变的值
            var animationValue by remember { mutableStateOf(0f) }

            LaunchedEffect(key1 = mViewMode.mAnimationState) {
                //记录开始时间，这里减去playTime，是为了暂停恢复后能够重新重原理的进度重新播放
                val startTime = withFrameNanos { it } - playTime
                //当音乐处于播放状态时不断地计算当前的播放时间以及获取当前播放时间对应的动画值
                while (mViewMode.mAnimationState == MusicDetailViewModel.RUNNING) {
                    //计算当前的播放时间
                    playTime = withFrameNanos { it } - startTime
                    //获取当前播放时间对应的动画值
                    animationValue = animation.getValueFromNanos(playTime)
                }
            }

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
                        .rotate(animationValue)
                )
            }
        }
        LaunchedEffect(key1 = pagerState) {
            //通过Flow流监听页面变化
            snapshotFlow {
                pagerState.currentPage
            }.collect { page ->
                mViewMode.mSelectPlayUrl = mViewMode.mMusicList[page].poster
                mViewMode.playMusicWithList(page)
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

    //底部控制器控件
    @Composable
    fun OperateView() {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(mViewMode.mCurrTimeText, style = TextStyle(color = White, fontSize = 10.sp))
                Slider(
                    value = mViewMode.mProgress,
                    onValueChange = { pos ->
                        mViewMode.mProgress = pos
                        mViewMode.seekTo((pos * mViewMode.mCurrentMusicTotalTime).toLong(), true)
                    },
                    Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = White,
                        inactiveTickColor = BgColor,
                        activeTrackColor = White
                    )
                )
                Text(mViewMode.mTotalTimeText, style = TextStyle(color = White, fontSize = 10.sp))
            }
            Box(contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.mipmap.ic_play_btn_prev),
                        contentDescription = null,
                        Modifier.clickable {
                            mViewMode.skipToPrevious()
                        }
                    )
                    Image(
                        painter = painterResource(mViewMode.mPlayOrPauseIcon),
                        contentDescription = null,
                        Modifier.clickable {
                           mViewMode.playOrPause()
                        }
                    )
                    Image(
                        painter = painterResource(R.mipmap.ic_play_btn_next),
                        contentDescription = null,
                        Modifier.clickable {
                           mViewMode.skipToNext()
                        }
                    )
                }
            }
        }
    }
}
