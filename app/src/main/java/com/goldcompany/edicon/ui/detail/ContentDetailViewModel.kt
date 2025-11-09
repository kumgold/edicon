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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val isFavorite: StateFlow<Boolean> = flow {
        if (contentId > 0) {
            when (contentType) {
                ContentType.IMAGE -> emitAll(localRepository.getImageAsFlow(contentId))
                ContentType.VIDEO -> emitAll(localRepository.getVideoAsFlow(contentId))
                else -> emit(null)
            }
        } else {
            emit(null)
        }
    }.map { entity ->
        // entity가 DB에 존재하고 isFavorite가 true일 때만 true
        entity != null && (entity as? ImageEntity)?.isFavorite ?: (entity as? VideoEntity)?.isFavorite ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        if (contentId > 0 && contentType != null) {
            fetchDetails(contentId, contentType)
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "잘못된 접근입니다.") }
        }
    }

    private fun fetchDetails(id: Int, contentType: ContentType) {
        viewModelScope.launch {
            when (contentType) {
                ContentType.IMAGE -> {
                    val result = networkRepository.getImageById(id)
                    result.onSuccess { image ->
                        _uiState.update { it.copy(isLoading = false, content = DetailContent.Image(image)) }
                    }.onFailure { error ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    }
                }
                ContentType.VIDEO -> {
                    val result = networkRepository.getVideoById(id)
                    result.onSuccess { video ->
                        _uiState.update { it.copy(isLoading = false, content = DetailContent.Video(video)) }
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
            val currentStatus = isFavorite.value

            when (content) {
                is DetailContent.Image -> {
                    // isFavorite 상태를 반전시켜 DB에 저장 (없으면 추가, 있으면 업데이트)
                    localRepository.saveImage(content.entity.copy(isFavorite = !currentStatus))
                }
                is DetailContent.Video -> {
                    localRepository.saveVideo(content.entity.copy(isFavorite = !currentStatus))
                }
            }
        }
    }
}