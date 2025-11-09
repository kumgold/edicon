package com.goldcompany.edicon.data.network.model

import com.goldcompany.edicon.data.local.image.ImageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageModel(
    val id: Int,
    val tags: String,
    @SerialName("webformatURL")
    val webFormatUrl: String,
    @SerialName("previewURL")
    val previewUrl: String,
    @SerialName("imageURL")
    val imageUrl: String? = null,
    val user: String,
    val views: Int,
    val downloads: Int,
    val likes: Int,
    val type: String
)

fun ImageModel.toEntity(): ImageEntity {
    return ImageEntity(
        id = id,
        tags = tags,
        webFormatUrl = webFormatUrl,
        previewUrl = previewUrl,
        imageUrl = imageUrl,
        user = user,
        views = views,
        downloads = downloads,
        likes = likes,
        type = type
    )
}