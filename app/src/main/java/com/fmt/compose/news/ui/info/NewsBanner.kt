package com.fmt.compose.news.ui.info

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import com.fmt.compose.news.R
import com.fmt.compose.news.ext.toPx
import com.fmt.compose.news.model.TopStoryModel
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

@Composable
fun NewsBanner(topStories: List<TopStoryModel>) {
    val context = LocalContext.current
    AndroidView(factory = {
        Banner<TopStoryModel, BannerImageAdapter<TopStoryModel>>(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 140.toPx())
            indicator = CircleIndicator(context)
            setAdapter(object : BannerImageAdapter<TopStoryModel>(topStories) {
                override fun onBindView(
                    holder: BannerImageHolder,
                    data: TopStoryModel,
                    position: Int,
                    size: Int
                ) {
                    holder.imageView.load(data.image)
                }
            })
            setOnBannerListener { data, _ ->
                run {
                    NewsDetailActivity.go(context, data.title, data.url)
                }
            }
        }
    }, update = {
        it.setIndicatorSelectedColorRes(R.color.purple_200)
        it.setDatas(topStories)
    })
}
