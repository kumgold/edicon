package com.goldcompany.edicon.ui.detail.model

import com.goldcompany.edicon.data.local.image.ImageEntity
import com.goldcompany.edicon.data.local.video.VideoEntity
import com.goldcompany.edicon.ui.util.ContentType

sealed class DetailContent {
    abstract val id: Int
    abstract val thumbnailUrl: String?
    abstract val contentUrl: String?
    abstract val tags: String
    abstract val user: String
    abstract val type: ContentType
    abstract val views: Int
    abstract val downloads: Int
    abstract val likes: Int

    data class Image(val entity: ImageEntity) : DetailContent() {
        override val id: Int get() = entity.id
        override val thumbnailUrl: String? get() = entity.previewUrl ?: entity.webFormatUrl
        override val contentUrl: String? get() = entity.imageUrl ?: entity.webFormatUrl
        override val tags: String get() = entity.tags
        override val user: String get() = entity.user
        override val type: ContentType get() = ContentType.IMAGE
        override val views: Int get() = entity.views
        override val downloads: Int get() = entity.downloads
        override val likes: Int get() = entity.likes
    }

    data class Video(val entity: VideoEntity) : DetailContent() {
        override val id: Int get() = entity.id
        override val thumbnailUrl: String get() = entity.thumbnailUrl
        override val contentUrl: String get() = entity.largeVideoUrl
        override val tags: String get() = entity.tags
        override val user: String get() = entity.user
        override val type: ContentType get() = ContentType.VIDEO
        override val views: Int get() = entity.views
        override val downloads: Int get() = entity.downloads
        override val likes: Int get() = entity.likes
    }
}