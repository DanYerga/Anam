package kz.anam.data.models

/**
 * Категории медицинских документов
 */
enum class DocumentCategory {
    BLOOD_TEST,    // Анализы крови
    ULTRASOUND,    // УЗИ
    CERTIFICATE,   // Справки
    OTHER;         // Другое

    val displayName: String
        get() = when (this) {
            BLOOD_TEST -> "Blood test"
            ULTRASOUND -> "Ultrasound results"
            CERTIFICATE -> "Reports"
            OTHER -> "Other"
        }
}