package kz.anam.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Document - medical documents storage (like Kaspi Documents)
 * Stores references to medical reports, analysis results, etc.
 */
@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,              // e.g. "УЗИ 12 недель", "Анализ крови"
    val documentType: DocumentType,
    val filePath: String,           // Local file path or cloud URL
    val uploadDate: Long,           // Unix timestamp
    val documentDate: Long,         // Date of the actual document
    val notes: String = "",
    val tags: List<String> = emptyList()
)

enum class DocumentType {
    ULTRASOUND,
    BLOOD_TEST,
    URINE_TEST,
    MEDICAL_REPORT,
    PRESCRIPTION,
    VACCINATION,
    OTHER
}

fun DocumentType.displayName(): String = when(this) {
    DocumentType.ULTRASOUND -> "Ultrasound"
    DocumentType.BLOOD_TEST -> "Blood test"
    DocumentType.URINE_TEST -> "Urine test"
    DocumentType.MEDICAL_REPORT -> "Medical reports"
    DocumentType.PRESCRIPTION -> "Prescription"
    DocumentType.VACCINATION -> "Vaccination"
    DocumentType.OTHER -> "Other"
}