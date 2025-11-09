package com.goldcompany.edicon.data.di

import android.content.Context
import androidx.room.Room
import com.goldcompany.edicon.data.local.AppDatabase
import com.goldcompany.edicon.data.local.image.ImageDao
import com.goldcompany.edicon.data.local.video.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "edicon_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageDao(appDatabase: AppDatabase): ImageDao {
        return appDatabase.imageDao()
    }

    @Provides
    @Singleton
    fun provideVideoDao(appDatabase: AppDatabase): VideoDao {
        return appDatabase.videoDao()
    }
}