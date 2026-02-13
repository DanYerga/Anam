package kz.anam.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SymptomEntry - core data model for symptom logging
 * Critical for AI pattern detection (food → symptoms)
 */
@Entity(tableName = "symptom_entries")
data class SymptomEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val symptomType: SymptomType,
    val severity: SymptomSeverity,    // 1-5 scale
    val timestamp: Long,               // Unix timestamp
    val duration: Int = 0,             // Duration in minutes
    val notes: String = "",

    // For AI correlation
    val triggeredByFoodId: Long? = null  // Link to suspected food
)

enum class SymptomType {
    NAUSEA,           // Тошнота
    VOMITING,         // Рвота
    HEADACHE,         // Головная боль
    DIZZINESS,        // Головокружение
    FATIGUE,          // Усталость
    HEARTBURN,        // Изжога
    STOMACH_PAIN,     // Боль в животе
    CONSTIPATION,     // Запор
    DIARRHEA,         // Диарея
    SWELLING,         // Отеки
    OTHER             // Другое
}

enum class SymptomSeverity(val value: Int, val label: String) {
    MILD(1, "Mild"),
    MODERATE(2, "Moderate"),
    SEVERE(3, "Severe"),
    VERY_SEVERE(4, "Very severe"),
    CRITICAL(5, "Critical")
}

// Extension to get display name
fun SymptomType.displayName(): String = when(this) {
    SymptomType.NAUSEA -> "Nausea"
    SymptomType.VOMITING -> "Vomiting"
    SymptomType.HEADACHE -> "Headache"
    SymptomType.DIZZINESS -> "Dizziness"
    SymptomType.FATIGUE -> "Fatigue"
    SymptomType.HEARTBURN -> "Heartburn"
    SymptomType.STOMACH_PAIN -> "Stomach pain"
    SymptomType.CONSTIPATION -> "Consipation"
    SymptomType.DIARRHEA -> "Diarrhea"
    SymptomType.SWELLING -> "Swelling"
    SymptomType.OTHER -> "Other"
}