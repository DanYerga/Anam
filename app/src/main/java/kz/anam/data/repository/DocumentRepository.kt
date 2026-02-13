package kz.anam.data.repository

import kotlinx.coroutines.flow.Flow
import kz.anam.data.database.dao.DocumentDao
import kz.anam.data.models.Document
import kz.anam.data.models.DocumentType

/**
 * Repository для работы с документами (анализы, УЗИ, справки)
 * Путь: app/src/main/java/kz/anam/data/repository/DocumentRepository.kt
 *
 * СОВМЕСТИМ С ВАШЕЙ МОДЕЛЬЮ Document!
 */
class DocumentRepository(
    private val documentDao: DocumentDao
) {

    /**
     * Получить все документы (отсортированы по дате документа)
     */
    fun getAllDocuments(): Flow<List<Document>> {
        return documentDao.getAllDocuments()
    }

    /**
     * Получить документы по типу
     */
    fun getDocumentsByType(type: DocumentType): Flow<List<Document>> {
        return documentDao.getDocumentsByType(type)
    }

    /**
     * Добавить новый документ
     */
    suspend fun addDocument(
        title: String,
        documentType: DocumentType,
        filePath: String,
        documentDate: Long = System.currentTimeMillis(),
        notes: String = "",
        tags: List<String> = emptyList()
    ): Long {
        val document = Document(
            id = 0, // Room автоматически присвоит ID
            title = title,
            documentType = documentType,
            filePath = filePath,
            uploadDate = System.currentTimeMillis(),
            documentDate = documentDate,
            notes = notes,
            tags = tags
        )
        return documentDao.insert(document)
    }

    /**
     * Обновить документ
     */
    suspend fun updateDocument(document: Document) {
        documentDao.update(document)
    }

    /**
     * Удалить документ
     */
    suspend fun deleteDocument(document: Document) {
        documentDao.delete(document)
    }

    /**
     * Удалить документ по ID
     */
    suspend fun deleteDocumentById(documentId: Long) {
        val document = documentDao.getDocumentById(documentId)
        document?.let { documentDao.delete(it) }
    }

    /**
     * Получить документ по ID
     */
    suspend fun getDocumentById(documentId: Long): Document? {
        return documentDao.getDocumentById(documentId)
    }

    /**
     * Получить количество документов
     */
    suspend fun getDocumentCount(): Int {
        return documentDao.getDocumentCount()
    }
}