package com.goldcompany.edicon.ui.favorite.model

import com.goldcompany.edicon.data.local.image.ImageEntity
import com.goldcompany.edicon.data.local.video.VideoEntity
import com.goldcompany.edicon.ui.util.ContentType

sealed class FavoriteUiItem {
    abstract val id: Int
    abstract val thumbnailUrl: String?
    abstract val largeContentUrl: String?
    abstract val tags: String
    abstract val user: String
    abstract val type: ContentType

    data class Image(val entity: ImageEntity) : FavoriteUiItem() {
        override val id: Int get() = entity.id
        override val thumbnailUrl: String? get() = entity.previewUrl ?: entity.webFormatUrl
        override val largeContentUrl: String? get() = entity.imageUrl ?: entity.webFormatUrl
        override val tags: String get() = entity.tags
        override val user: String get() = entity.user
        override val type: ContentType get() = ContentType.IMAGE
    }

    data class Video(val entity: VideoEntity) : FavoriteUiItem() {
        override val id: Int get() = entity.id
        override val thumbnailUrl: String get() = entity.thumbnailUrl
        override val largeContentUrl: String get() = entity.largeVideoUrl
        override val tags: String get() = entity.tags
        override val user: String get() = entity.user
        override val type: ContentType get() = ContentType.VIDEO
    }
}