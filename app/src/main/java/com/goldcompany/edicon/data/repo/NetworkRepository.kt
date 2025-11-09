package com.goldcompany.edicon.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.goldcompany.edicon.data.local.AppDatabase
import com.goldcompany.edicon.data.local.image.ImageEntity
import com.goldcompany.edicon.data.local.video.VideoEntity
import com.goldcompany.edicon.data.network.PixabayApiService
import com.goldcompany.edicon.data.network.model.PixabayResponse
import com.goldcompany.edicon.data.network.model.VideoModel
import com.goldcompany.edicon.data.network.model.toEntity
import com.goldcompany.edicon.data.network.paging.ImagePagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 네트워크(Pixabay API)와 상호작용하는 Repository.
 * Hilt에 의해 싱글턴으로 관리
 * @param pixabayApiService Retrofit을 통해 생성된 API 서비스 인터페이스
 */
@Singleton
class NetworkRepository @Inject constructor(
    private val pixabayApiService: PixabayApiService
) {

    /**
     * 주어진 검색어로 이미지를 검색
     * 네트워크 오류가 발생할 경우를 대비해 Result 래퍼로 감싸 반환
     *
     * @param query 사용자가 입력한 검색어
     * @return 검색 성공 시 PixabayResponse<ImageModel>를 포함한 Result.success, 실패 시 예외를 포함한 Result.failure
     */
    fun searchImages(query: String): Flow<PagingData<ImageEntity>> {
        val seenIds = ConcurrentHashMap.newKeySet<Int>()

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40
            ),
            pagingSourceFactory = { ImagePagingSource(pixabayApiService, query) }
        ).flow
            .map { pagingData ->
                pagingData.filter { imageModel ->
                    seenIds.add(imageModel.id)
                }
            }
            .map { pagingDataOfModels ->
                pagingDataOfModels.map { imageModel ->
                    imageModel.toEntity()
                }
            }
    }

    suspend fun getImageById(id: Int): Result<ImageEntity> {
        return try {
            val response = pixabayApiService.getImageById(id = id)
            response.hits.firstOrNull()?.let {
                Result.success(it).map { model -> model.toEntity() }
            } ?: Result.failure(Exception("Image not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 주어진 검색어로 비디오를 검색
     * 네트워크 오류가 발생할 경우를 대비해 Result 래퍼로 감싸 반환
     *
     * @param query 사용자가 입력한 검색어
     * @return 검색 성공 시 PixabayResponse<VideoModel>를 포함한 Result.success, 실패 시 예외를 포함한 Result.failure
     */
    suspend fun searchVideos(query: String): Result<PixabayResponse<VideoModel>> {
        return try {
            val response = pixabayApiService.searchVideos(query = query)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVideoById(id: Int): Result<VideoEntity> {
        return try {
            val response = pixabayApiService.getVideoById(id = id)
            response.hits.firstOrNull()?.let {
                Result.success(it).map { model -> model.toEntity() }
            } ?: Result.failure(Exception("Video not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}