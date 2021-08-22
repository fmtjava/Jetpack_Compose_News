package com.fmt.compose.news.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.fmt.compose.news.R
import com.fmt.compose.news.ext.showToast
import com.fmt.compose.news.ui.music.MusicDetailActivity
import com.fmt.compose.news.ui.music.db.Music
import com.lzx.starrysky.OnPlayProgressListener
import com.lzx.starrysky.SongInfo
import com.lzx.starrysky.StarrySky
import com.lzx.starrysky.manager.PlaybackStage
import com.lzx.starrysky.utils.formatTime
import java.util.*
import kotlin.math.roundToInt

class MusicDetailViewModel : ViewModel() {

    lateinit var mMusicList: ArrayList<Music>
    private val mMediaList by lazy { mutableListOf<SongInfo>() }
    var mPlayItemPosition = 0//当前播放的音乐对应的下标
    var mCurrentMusicTotalTime = 0L//当前音乐的总时长
    var mSelectItemPosition by mutableStateOf(0)//选择播放的音乐对应的下标
    var mSelectPlayUrl by mutableStateOf("")//播放的音乐对应的播放地址
    var mPlayOrPauseIcon by mutableStateOf(R.mipmap.ic_play_btn_pause)//播放的按钮的状态
    var mProgress by mutableStateOf(0f)//当前音乐的播放进度
    var mCurrTimeText by mutableStateOf("")//当前音乐的播放时间
    var mTotalTimeText by mutableStateOf("")//当前音乐的总时长
    var mAnimationState by mutableStateOf(RUNNING)//当前音乐的播放状态

    companion object {
        const val RUNNING = 0
        const val PAUSED = 1
    }

    fun init(intent: Intent, owner: LifecycleOwner) {
        mPlayItemPosition = intent.getIntExtra(MusicDetailActivity.PLAY_ITEM_POSITION, 0)
        mSelectItemPosition = mPlayItemPosition
        mMusicList = intent.getParcelableArrayListExtra(MusicDetailActivity.MUSIC_LIST)!!
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
        initStarrySky(owner)
    }

    //初始化音乐播放器
    private fun initStarrySky(owner: LifecycleOwner) {
        playMusicWithList(mPlayItemPosition)
        //监听播放状态
        StarrySky.with().playbackState().observe(owner) {
            when (it.stage) {
                PlaybackStage.PLAYING -> {
                    mAnimationState = RUNNING
                    mPlayOrPauseIcon = R.mipmap.ic_play_btn_pause
                }
                //切歌
                PlaybackStage.SWITCH -> it.songInfo?.let { songInfo ->
                    mSelectPlayUrl = songInfo.songCover
                    mSelectItemPosition = findPositionById(songInfo.songId)
                }
                PlaybackStage.PAUSE,
                PlaybackStage.IDLE -> {
                    mAnimationState = PAUSED
                    mPlayOrPauseIcon = R.mipmap.ic_play_btn_play
                }
                PlaybackStage.ERROR -> {
                    (owner as Activity).showToast(it.errorMsg)
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
    fun playMusicWithList(playItemPosition: Int) {
        StarrySky.with().playMusic(mMediaList, playItemPosition)
    }

    //跳转到指定位置
    fun seekTo(pos: Long, isPlayWhenPaused: Boolean = true) {
        StarrySky.with()
            .seekTo(pos = pos, isPlayWhenPaused = isPlayWhenPaused)
    }

    //跳转到上一曲
    fun skipToPrevious(){
        StarrySky.with().skipToPrevious()
    }

    //跳转到下一曲
    fun skipToNext(){
        StarrySky.with().skipToNext()
    }

    //播放或暂停
    fun playOrPause(){
        if (StarrySky.with().isPlaying()) {
            StarrySky.with().pauseMusic()
        } else {
            StarrySky.with().restoreMusic()
        }
    }

}