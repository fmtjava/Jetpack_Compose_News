package com.fmt.compose.news.model

class MovieModel(val itemList: List<MovieItemModel> = listOf())

class MovieItemModel(
    val data: MovieItem
)

class MovieItem(
    val category: String,
    val playUrl: String,
    val title: String,
    val cover: Cover,
    val duration: Int
)

class Cover(val feed: String)
