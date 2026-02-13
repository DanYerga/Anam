package kz.anam.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kz.anam.data.models.Document
import kz.anam.data.models.DocumentType
import kz.anam.data.repository.DocumentRepository

/**
 * ViewModel для управления документами
 * Путь: app/src/main/java/kz/anam/viewmodels/DocumentsViewModel.kt
 *
 * СОВМЕСТИМ С DocumentType (не DocumentCategory)!
 */

data class DocumentsUiState(
    val documents: List<Document> = emptyList(),
    val selectedType: DocumentType? = null,
    val uploadType: DocumentType = DocumentType.OTHER
)

class DocumentsViewModel(
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentsUiState())
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            documentRepository.getAllDocuments().collect { documents ->
                _uiState.value = _uiState.value.copy(
                    documents = filterDocuments(documents, _uiState.value.selectedType)
                )
            }
        }
    }

    fun filterByType(type: DocumentType?) {
        viewModelScope.launch {
            if (type == null) {
                documentRepository.getAllDocuments().collect { documents ->
                    _uiState.value = _uiState.value.copy(
                        documents = documents,
                        selectedType = null
                    )
                }
            } else {
                documentRepository.getDocumentsByType(type).collect { documents ->
                    _uiState.value = _uiState.value.copy(
                        documents = documents,
                        selectedType = type
                    )
                }
            }
        }
    }

    private fun filterDocuments(
        documents: List<Document>,
        type: DocumentType?
    ): List<Document> {
        return if (type == null) {
            documents
        } else {
            documents.filter { it.documentType == type }
        }
    }

    fun setUploadType(type: DocumentType) {
        _uiState.value = _uiState.value.copy(uploadType = type)
    }

    fun addDocument(uri: Uri) {
        viewModelScope.launch {
            // Извлекаем имя файла из URI
            val fileName = uri.lastPathSegment ?: "document_${System.currentTimeMillis()}"

            documentRepository.addDocument(
                title = fileName,
                documentType = _uiState.value.uploadType,
                filePath = uri.toString(),
                documentDate = System.currentTimeMillis(),
                notes = ""
            )
        }
    }

    fun deleteDocument(documentId: Long) {
        viewModelScope.launch {
            documentRepository.deleteDocumentById(documentId)
        }
    }
}