package com.goldcompany.edicon.data.local.image

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val id: Int,
    val tags: String,
    val webFormatUrl: String? = null,
    val previewUrl: String? = null,
    val imageUrl: String? = null,
    val user: String,
    val views: Int,
    val downloads: Int,
    val likes: Int,
    val type: String,
    val isFavorite: Boolean = false
)