package com.goldcompany.edicon.data.network

import com.goldcompany.edicon.BuildConfig
import com.goldcompany.edicon.data.network.model.ImageModel
import com.goldcompany.edicon.data.network.model.PixabayResponse
import com.goldcompany.edicon.data.network.model.VideoModel
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiService {
    @GET("api/")
    suspend fun searchImages(
        @Query("key") apiKey: String = BuildConfig.PIXABAY_API_KEY,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): PixabayResponse<ImageModel>

    @GET("api/")
    suspend fun getImageById(
        @Query("key") apiKey: String = BuildConfig.PIXABAY_API_KEY,
        @Query("id") id: Int
    ): PixabayResponse<ImageModel>

    @GET("api/videos/")
    suspend fun searchVideos(
        @Query("key") apiKey: String = BuildConfig.PIXABAY_API_KEY,
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 3
    ): PixabayResponse<VideoModel>

    @GET("api/videos/")
    suspend fun getVideoById(
        @Query("key") apiKey: String = BuildConfig.PIXABAY_API_KEY,
        @Query("id") id: Int
    ): PixabayResponse<VideoModel>
}