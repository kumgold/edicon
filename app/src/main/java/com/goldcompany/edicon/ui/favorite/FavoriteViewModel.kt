package com.goldcompany.edicon.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.edicon.data.repo.LocalRepository
import com.goldcompany.edicon.ui.favorite.model.FavoriteUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : ViewModel() {
    val favoriteItems: StateFlow<List<FavoriteUiItem>> =
        combine(
            localRepository.getFavoriteImages(),
            localRepository.getFavoriteVideos()
        ) { images, videos ->
            val combinedList = mutableListOf<FavoriteUiItem>()
            combinedList.addAll(images.map { FavoriteUiItem.Image(it) })
            combinedList.addAll(videos.map { FavoriteUiItem.Video(it) })
            combinedList
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeFromFavorites(item: FavoriteUiItem) {
        viewModelScope.launch {
            when (item) {
                is FavoriteUiItem.Image -> localRepository.updateImageFavoriteStatus(item.id, false)
                is FavoriteUiItem.Video -> localRepository.updateVideoFavoriteStatus(item.id, false)
            }
        }
    }
}