package kz.anam.data.models

/**
 * DailyStats - aggregated data for HomeScreen dashboard
 * Shows today's summary
 */
data class DailyStats(
    val date: Long,                    // Unix timestamp of day
    val totalMeals: Int,               // Number of meals logged
    val totalSymptoms: Int,            // Number of symptoms recorded
    val waterIntake: Int,              // Glasses of water
    val criticalSymptoms: Boolean,     // Any critical symptoms?
    val aiWarnings: List<AIWarning>    // AI-detected warnings
)

data class AIWarning(
    val severity: WarningSeverity,
    val title: String,
    val description: String,
    val actionRequired: Boolean
)

enum class WarningSeverity {
    INFO,        // Информация
    WARNING,     // Предупреждение
    CRITICAL     // Критично - нужен врач
}