package com.goldcompany.edicon.data.local.video

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id")
    fun getVideoAsFlow(id: Int): Flow<VideoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteVideo(id: Int)

    @Query("SELECT * FROM videos WHERE isFavorite = 1")
    fun getFavoriteVideos(): Flow<List<VideoEntity>>

    @Query("UPDATE videos SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("SELECT id FROM videos WHERE isFavorite = 1")
    fun getFavoriteVideoIds(): Flow<List<Int>>
}