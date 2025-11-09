package com.goldcompany.edicon.data.repo

import com.goldcompany.edicon.data.local.image.ImageDao
import com.goldcompany.edicon.data.local.image.ImageEntity
import com.goldcompany.edicon.data.local.video.VideoDao
import com.goldcompany.edicon.data.local.video.VideoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val imageDao: ImageDao,
    private val videoDao: VideoDao
) {

    // --- Image 관련 메소드 ---

    /**
     * 즐겨찾기된 모든 이미지 목록을 Flow 형태로 가져온다.
     */
    fun getFavoriteImages(): Flow<List<ImageEntity>> = imageDao.getFavoriteImages()

    /**
     * 특정 이미지를 데이터베이스에 저장(삽입 또는 교체)
     */
    suspend fun saveImage(image: ImageEntity) {
        imageDao.insertImage(image)
    }

    /**
     * 특정 이미지의 즐겨찾기 상태를 업데이트
     */
    suspend fun updateImageFavoriteStatus(id: Int, isFavorite: Boolean) {
        imageDao.updateFavoriteStatus(id, isFavorite)
    }

    /**
     * 즐겨찾기 아이템 id 반환
     */
    fun getFavoriteImageIds(): Flow<Set<Int>> = imageDao.getFavoriteImageIds().map { it.toSet() }

    /**
     * id로 즐겨찾기 이미지를 가져온다.
     */
    fun getImageAsFlow(id: Int): Flow<ImageEntity?> = imageDao.getImageAsFlow(id)

    // --- Video 관련 메소드 ---

    /**
     * 즐겨찾기된 모든 비디오 목록을 Flow 형태로 가져온다.
     */
    fun getFavoriteVideos(): Flow<List<VideoEntity>> = videoDao.getFavoriteVideos()

    /**
     * 특정 비디오를 데이터베이스에 저장(삽입 또는 교체)
     */
    suspend fun saveVideo(video: VideoEntity) {
        videoDao.insertVideo(video)
    }

    /**
     * 특정 비디오의 즐겨찾기 상태를 업데이트
     */
    suspend fun updateVideoFavoriteStatus(id: Int, isFavorite: Boolean) {
        videoDao.updateFavoriteStatus(id, isFavorite)
    }

    /**
     * 즐겨찾기 아이템 id 반환
     */
    fun getFavoriteVideoIds(): Flow<Set<Int>> = videoDao.getFavoriteVideoIds().map { it.toSet() }

    /**
     * id로 즐겨찾기 동영상을 가져온다.
     */
    fun getVideoAsFlow(id: Int): Flow<VideoEntity?> = videoDao.getVideoAsFlow(id)
}