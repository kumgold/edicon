package com.goldcompany.edicon.data.local.video

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: Int,
    val tags: String,
    val largeVideoUrl: String,
    val thumbnailUrl: String,
    val user: String,
    val pageUrl: String,
    val views: Int,
    val downloads: Int,
    val likes: Int,
    val type: String,
    val isFavorite: Boolean = false
)