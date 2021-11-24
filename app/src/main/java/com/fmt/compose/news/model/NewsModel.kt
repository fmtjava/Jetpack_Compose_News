package com.fmt.compose.news.model

class PageModel<T>(
    val totalNum: Int,
    val list: T
)

class NewsModelModel(
    val top_stories: List<TopStoryModel> = listOf(),
    val stories: MutableList<StoryModel> = mutableListOf()
)

class TopStoryModel(
    val id: Int,
    val hint: String,
    val url: String,
    val title: String,
    val image: String
)

class StoryModel(
    val id: Int = 0,
    val hint: String = "",
    val url: String = "",
    val title: String = "",
    val images: List<String> = listOf()
)