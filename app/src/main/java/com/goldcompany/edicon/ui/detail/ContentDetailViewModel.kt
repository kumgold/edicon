package com.goldcompany.edicon.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.edicon.data.local.image.ImageEntity
import com.goldcompany.edicon.data.local.video.VideoEntity
import com.goldcompany.edicon.data.repo.LocalRepository
import com.goldcompany.edicon.data.repo.NetworkRepository
import com.goldcompany.edicon.ui.detail.model.DetailContent
import com.goldcompany.edicon.ui.util.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val content: DetailContent? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class ContentDetailViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val localRepository: LocalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    private val contentId: Int = savedStateHandle["id"] ?: 0
    private val contentType: ContentType? = savedStateHandle.get<ContentType>("contentType")

    init {
        if (contentId > 0 && contentType != null) {
            fetchDetails(contentId, contentType)
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "잘못된 접근입니다.") }
        }
    }

    private fun fetchDetails(id: Int, contentType: ContentType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val dbFlow = when (contentType) {
                ContentType.IMAGE -> localRepository.getImageAsFlow(id).map { it?.let { DetailContent.Image(it) } }
                ContentType.VIDEO -> localRepository.getVideoAsFlow(id).map { it?.let { DetailContent.Video(it) } }
            }

            dbFlow.collectLatest { contentFromDb ->
                if (contentFromDb != null) {
                    _uiState.update { it.copy(isLoading = false, content = contentFromDb) }
                } else {
                    val result = when (contentType) {
                        ContentType.IMAGE -> networkRepository.getImageById(id)
                        ContentType.VIDEO -> networkRepository.getVideoById(id)
                    }

                    result.onSuccess { entity ->
                        when (entity) {
                            is ImageEntity -> {
                                _uiState.update { it.copy(isLoading = false, content = DetailContent.Image(entity)) }
                            }
                            is VideoEntity -> {
                                _uiState.update { it.copy(isLoading = false, content = DetailContent.Video(entity)) }
                            }
                        }
                    }.onFailure { error ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val content = _uiState.value.content ?: return@launch
            val isCurrentlyFavorite = when(content) {
                is DetailContent.Image -> content.entity.isFavorite
                is DetailContent.Video -> content.entity.isFavorite
            }

            when (content) {
                is DetailContent.Image -> {
                    localRepository.updateImageFavoriteStatus(content.id, !isCurrentlyFavorite)
                }
                is DetailContent.Video -> {
                    localRepository.updateVideoFavoriteStatus(content.id, !isCurrentlyFavorite)
                }
            }
        }
    }
}