package kz.anam.data.repository

import kotlinx.coroutines.flow.first
import kz.anam.data.models.FoodEntry
import kz.anam.data.models.SymptomEntry
import kz.anam.data.models.SymptomSeverity
import java.util.concurrent.TimeUnit

/**
 * Repository для AI аналитики и паттерн-анализа
 * Путь: app/src/main/java/kz/anam/data/repository/InsightsRepository.kt
 *
 * СОВМЕСТИМ С timestamp: Long моделями!
 */

data class FoodTrigger(
    val foodName: String,
    val symptomType: String,
    val correlation: Double,  // 0.0 - 1.0
    val occurrences: Int
)

data class RiskAssessment(
    val level: RiskLevel,
    val score: Int,  // 0-100
    val reasons: List<String>,
    val recommendations: List<String>
)

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

class InsightsRepository(
    private val foodRepository: FoodRepository,
    private val symptomRepository: SymptomRepository,
    private val aiChatRepository: AIChatRepository
) {

    /**
     * Анализирует паттерны: какие продукты вызывают симптомы
     */
    suspend fun analyzeFoodTriggers(): List<FoodTrigger> {
        val triggers = mutableListOf<FoodTrigger>()

        try {
            val allFood = foodRepository.getAllEntries().first()
            val allSymptoms = symptomRepository.getAllEntries().first()

            // Группируем по дням (округляем timestamp до начала дня)
            val foodByDay = allFood.groupBy { getDayTimestamp(it.timestamp) }
            val symptomsByDay = allSymptoms.groupBy { getDayTimestamp(it.timestamp) }

            // Для каждого продукта считаем корреляцию с симптомами
            val uniqueFoods = allFood.map { it.foodName.lowercase() }.distinct()

            uniqueFoods.forEach { foodName ->
                val daysWithFood = allFood
                    .filter { it.foodName.lowercase() == foodName }
                    .map { getDayTimestamp(it.timestamp) }
                    .toSet()

                // Проверяем симптомы в те же дни или на следующий день
                daysWithFood.forEach { dayTimestamp ->
                    val symptomsOnDay = symptomsByDay[dayTimestamp] ?: emptyList()
                    val symptomsNextDay = symptomsByDay[dayTimestamp + DAY_IN_MILLIS] ?: emptyList()

                    (symptomsOnDay + symptomsNextDay).forEach { symptom ->
                        val existing = triggers.find {
                            it.foodName.lowercase() == foodName &&
                                    it.symptomType == symptom.symptomType.name
                        }

                        if (existing != null) {
                            val index = triggers.indexOf(existing)
                            triggers[index] = existing.copy(occurrences = existing.occurrences + 1)
                        } else {
                            triggers.add(
                                FoodTrigger(
                                    foodName = foodName,
                                    symptomType = symptom.symptomType.name,
                                    correlation = 0.0,
                                    occurrences = 1
                                )
                            )
                        }
                    }
                }
            }

            // Рассчитываем корреляцию
            val updatedTriggers = triggers.map { trigger ->
                val totalFoodOccurrences = allFood
                    .count { it.foodName.lowercase() == trigger.foodName.lowercase() }
                    .toDouble()

                if (totalFoodOccurrences > 0) {
                    val correlation = trigger.occurrences / totalFoodOccurrences
                    trigger.copy(correlation = correlation)
                } else {
                    trigger
                }
            }

            // Фильтруем: только если корреляция > 40% и хотя бы 2 случая
            return updatedTriggers
                .filter { it.correlation >= 0.4 && it.occurrences >= 2 }
                .sortedByDescending { it.correlation }

        } catch (e: Exception) {
            return emptyList()
        }
    }

    /**
     * Оценивает текущий риск
     */
    suspend fun assessRisk(): RiskAssessment {
        try {
            // Получаем симптомы за последние 7 дней
            val sevenDaysAgo = System.currentTimeMillis() - (7 * DAY_IN_MILLIS)
            val recentSymptoms = symptomRepository.getAllEntries().first()
                .filter { it.timestamp >= sevenDaysAgo }

            var score = 0
            val reasons = mutableListOf<String>()
            val recommendations = mutableListOf<String>()

            // Анализ частоты симптомов
            val symptomsPerDay = recentSymptoms
                .groupBy { getDayTimestamp(it.timestamp) }
                .mapValues { it.value.size }

            val avgSymptomsPerDay = if (symptomsPerDay.isNotEmpty()) {
                symptomsPerDay.values.average()
            } else {
                0.0
            }

            when {
                avgSymptomsPerDay > 5 -> {
                    score += 40
                    reasons.add("High symptom frequency (${avgSymptomsPerDay.toInt()}/day)")
                }
                avgSymptomsPerDay > 3 -> {
                    score += 25
                    reasons.add("Moderate symptom frequency")
                }
            }

            // Анализ тяжести
            val severeSymptoms = recentSymptoms.count { it.severity == SymptomSeverity.SEVERE }
            if (severeSymptoms > 0) {
                score += severeSymptoms * 15
                reasons.add("Harsh symptoms ($severeSymptoms per week)")
            }

            // Анализ рвоты
            val vomitingCount = recentSymptoms.count {
                it.symptomType.name.contains("VOMITING", ignoreCase = true)
            }
            if (vomitingCount > 10) {
                score += 30
                reasons.add("Frequent nausea (dehydration risk)")
                recommendations.add("Drink more water")
                recommendations.add("Go to the doctor")
            }

            // Определяем уровень риска
            val level = when {
                score >= 80 -> RiskLevel.CRITICAL
                score >= 60 -> RiskLevel.HIGH
                score >= 30 -> RiskLevel.MEDIUM
                else -> RiskLevel.LOW
            }

            // Базовые рекомендации
            if (level == RiskLevel.LOW) {
                recommendations.add("Proceed writing your nutrition diary")
                recommendations.add("Avoid recognized triggers")
            } else if (level in listOf(RiskLevel.HIGH, RiskLevel.CRITICAL)) {
                recommendations.add("⚠️Go to the doctor URGENTLY")
                recommendations.add("Show your symptoms history")
            }

            return RiskAssessment(
                level = level,
                score = score.coerceIn(0, 100),
                reasons = reasons.ifEmpty { listOf("A little data to analyze,some data will appear later.") },
                recommendations = recommendations.ifEmpty { listOf("Proceed writing diary") }
            )

        } catch (e: Exception) {
            return RiskAssessment(
                level = RiskLevel.LOW,
                score = 0,
                reasons = listOf("A little data to analyze,some data will appear later."),
                recommendations = listOf("Proceed writing diary")
            )
        }
    }

    /**
     * Получает AI объяснение паттернов
     */
    suspend fun getAIExplanation(triggers: List<FoodTrigger>): Result<String> {
        if (triggers.isEmpty()) {
            return Result.success("No obvious triggers have been identified yet. Keep journaling!")
        }

        val prompt = buildInsightsPrompt(triggers)
        return aiChatRepository.sendMessage(prompt, includeContext = false)
    }

    private fun buildInsightsPrompt(triggers: List<FoodTrigger>): String {
        val triggersText = triggers.joinToString("\n") {
            "- ${it.foodName} → ${it.symptomType} (${(it.correlation * 100).toInt()}% correlation, ${it.occurrences} cases)"
        }

        return """
Analyze the following patterns between food and symptoms in a pregnant woman:

$triggersText

Explain:
1. Why these foods might cause a reaction
2. What can I eat instead?
3. Should I be concerned?

Be specific and helpful. Format: short paragraphs, clear language.
        """.trimIndent()
    }

    /**
     * Округляет timestamp до начала дня
     */
    private fun getDayTimestamp(timestamp: Long): Long {
        return (timestamp / DAY_IN_MILLIS) * DAY_IN_MILLIS
    }

    companion object {
        private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    }
}

