package com.goldcompany.edicon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.goldcompany.edicon.data.local.image.ImageDao
import com.goldcompany.edicon.data.local.image.ImageEntity
import com.goldcompany.edicon.data.local.video.VideoDao
import com.goldcompany.edicon.data.local.video.VideoEntity

@Database(
    entities = [ImageEntity::class, VideoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun videoDao(): VideoDao
}