package com.fmt.compose.news.ui.music.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "music")
data class Music(
    @PrimaryKey @ColumnInfo(name = "id") val musicId: String,
    val name: String,
    val poster: String,
    val path: String,
    val author: String
):Parcelable