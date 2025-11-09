package com.goldcompany.edicon.data.local.image

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM images")
    fun getAllImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE id = :id")
    fun getImageAsFlow(id: Int): Flow<ImageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Query("DELETE FROM images WHERE id = :id")
    suspend fun deleteImage(id: Int)

    @Query("SELECT * FROM images WHERE isFavorite = 1")
    fun getFavoriteImages(): Flow<List<ImageEntity>>

    @Query("UPDATE images SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("SELECT id FROM images WHERE isFavorite = 1")
    fun getFavoriteImageIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<ImageEntity>)

    @Query("DELETE FROM images WHERE isFavorite = 0")
    suspend fun clearAllImages()

    @Query("SELECT * FROM images ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, ImageEntity>
}