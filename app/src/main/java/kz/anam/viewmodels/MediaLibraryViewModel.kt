package kz.anam.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kz.anam.data.models.MediaContent
import kz.anam.data.models.MediaType
import kz.anam.data.repository.MediaRepository

/**
 * ViewModel для медиа библиотеки
 * Путь: app/src/main/java/kz/anam/viewmodels/MediaLibraryViewModel.kt
 */

data class MediaLibraryUiState(
    val allContent: List<MediaContent> = emptyList(),
    val filteredContent: List<MediaContent> = emptyList(),
    val selectedType: MediaType? = null,
    val pregnancyWeek: Int = 24
)

class MediaLibraryViewModel(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaLibraryUiState())
    val uiState: StateFlow<MediaLibraryUiState> = _uiState.asStateFlow()

    init {
        loadRecommendedContent()
    }

    fun loadRecommendedContent(pregnancyWeek: Int = 24) {
        val content = mediaRepository.getRecommendedContent(pregnancyWeek)
        _uiState.value = _uiState.value.copy(
            allContent = content,
            filteredContent = content,
            pregnancyWeek = pregnancyWeek
        )
    }

    fun filterByType(type: MediaType?) {
        val filtered = if (type == null) {
            _uiState.value.allContent
        } else {
            mediaRepository.getContentByType(type)
        }

        _uiState.value = _uiState.value.copy(
            filteredContent = filtered,
            selectedType = type
        )
    }
}