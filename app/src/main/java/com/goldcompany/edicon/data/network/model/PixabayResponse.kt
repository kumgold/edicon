package com.goldcompany.edicon.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PixabayResponse<T>(
    val total: Int,
    val totalHits: Int,
    val hits: List<T>
)