package com.goldcompany.edicon.data.network.model

import com.goldcompany.edicon.data.local.video.VideoEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoModel(
    val id: Int,
    val tags: String,
    val videos: VideoUrls,
    val user: String,
    @SerialName("pageURL")
    val pageUrl: String,
    val views: Int,
    val downloads: Int,
    val likes: Int,
    val type: String
)

@Serializable
data class VideoUrls(
    val large: VideoInfo,
    val medium: VideoInfo,
    val small: VideoInfo,
    val tiny: VideoInfo
)

@Serializable
data class VideoInfo(
    val url: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val thumbnail: String
)

fun VideoModel.toEntity(): VideoEntity {
    return VideoEntity(
        id = id,
        tags = tags,
        largeVideoUrl = videos.large.url,
        user = user,
        pageUrl = pageUrl,
        views = views,
        downloads = downloads,
        likes = likes,
        type = type,
        thumbnailUrl = videos.large.thumbnail
    )
}