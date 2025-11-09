package com.goldcompany.edicon.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.goldcompany.edicon.data.local.image.ImageEntity
import com.goldcompany.edicon.data.local.video.VideoEntity
import com.goldcompany.edicon.data.network.model.toEntity
import com.goldcompany.edicon.data.repo.LocalRepository
import com.goldcompany.edicon.data.repo.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val videoResult: VideoEntity? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val imagePagingData: Flow<PagingData<ImageEntity>> = _currentQuery
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            networkRepository.searchImages(query)
        }
        .cachedIn(viewModelScope)

    val favoriteImageIds: StateFlow<Set<Int>> = localRepository.getFavoriteImageIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val favoriteVideoIds: StateFlow<Set<Int>> = localRepository.getFavoriteVideoIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun search() {
        val query = _searchQuery.value
        if (query.isBlank()) return

        _currentQuery.value = query

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, videoResult = null, errorMessage = null) }
            val videoResult = networkRepository.searchVideos(_searchQuery.value)
            if (videoResult.isSuccess) {
                val videoEntity = videoResult.getOrNull()?.hits?.map { it.toEntity() }?.firstOrNull()
                _uiState.update { it.copy(isLoading = false, videoResult = videoEntity) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "검색 오류") }
            }
        }
    }

    fun toggleImageFavorite(image: ImageEntity, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                localRepository.updateImageFavoriteStatus(image.id, false)
            } else {
                localRepository.saveImage(image.copy(isFavorite = true))
            }
        }
    }

    fun toggleVideoFavorite(video: VideoEntity, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                localRepository.updateVideoFavoriteStatus(video.id, false)
            } else {
                localRepository.saveVideo(video.copy(isFavorite = true))
            }
        }
    }
}