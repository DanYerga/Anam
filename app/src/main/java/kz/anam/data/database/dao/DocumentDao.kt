package kz.anam.data.database.dao

import androidx.room.*
import kz.anam.data.models.Document
import kz.anam.data.models.DocumentType
import kotlinx.coroutines.flow.Flow

/**
 * DocumentDao - Data Access Object for medical documents
 * Like Kaspi Documents - everything in one place
 */
@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: Document): Long

    @Update
    suspend fun update(document: Document)

    @Delete
    suspend fun delete(document: Document)

    @Query("SELECT * FROM documents ORDER BY documentDate DESC")
    fun getAllDocuments(): Flow<List<Document>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): Document?

    @Query("SELECT * FROM documents WHERE documentType = :type ORDER BY documentDate DESC")
    fun getDocumentsByType(type: DocumentType): Flow<List<Document>>

    @Query("SELECT COUNT(*) FROM documents")
    suspend fun getDocumentCount(): Int
}