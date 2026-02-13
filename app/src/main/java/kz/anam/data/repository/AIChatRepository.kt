package kz.anam.data.repository

import kz.anam.data.api.ClaudeApiService
import kz.anam.data.api.ClaudeMessage
import kz.anam.data.api.ClaudeRequest
import kz.anam.data.models.FoodEntry
import kz.anam.data.models.SymptomEntry
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter

/**
 * AI Chat Repository - ИСПРАВЛЕННАЯ ВЕРСИЯ
 * Путь: app/src/main/java/kz/anam/data/repository/AIChatRepository.kt
 * ИСПРАВЛЕНО: работает с timestamp вместо LocalDate
 */
class AIChatRepository(
    private val claudeApi: ClaudeApiService,
    private val foodRepository: FoodRepository,
    private val symptomRepository: SymptomRepository,
    private val apiKey: String
) {

    private val systemPrompt = """
        Вы — AI-помощник для беременных женщин, специализирующийся на вопросах беременности, питания и симптомов.
        
        ВАЖНЫЕ ПРАВИЛА:
        1. Всегда напоминайте, что ваши советы не заменяют консультацию врача
        2. При серьёзных симптомах рекомендуйте обратиться к врачу
        3. Будьте эмпатичны и поддерживающи
        4. Давайте конкретные, практичные советы
        5. Отвечайте нa языке пользователя
        6. Будьте кратки и по делу
        
        Вы можете анализировать данные пользователя о питании и симптомах, чтобы давать персонализированные рекомендации.
    """.trimIndent()

    suspend fun sendMessage(
        userMessage: String,
        includeContext: Boolean = true
    ): Result<String> {
        return try {
            val contextMessage = if (includeContext) {
                buildUserContext()
            } else {
                ""
            }

            val fullMessage = if (contextMessage.isNotEmpty()) {
                """
                КОНТЕКСТ ПОЛЬЗОВАТЕЛЯ:
                $contextMessage
                
                ВОПРОС ПОЛЬЗОВАТЕЛЯ:
                $userMessage
                """.trimIndent()
            } else {
                userMessage
            }

            val messages = listOf(
                ClaudeMessage(role = "user", content = fullMessage)
            )

            val request = ClaudeRequest(
                messages = messages,
                max_tokens = 1024
            )

            val response = claudeApi.sendMessage(
                apiKey = apiKey,
                request = request
            )

            val responseText = response.content
                .firstOrNull { it.type == "text" }
                ?.text
                ?: "Sorry I am not able to response"

            Result.success(responseText)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * ✅ ИСПРАВЛЕНО: работает с timestamp
     */
    private suspend fun buildUserContext(): String {
        // Последние 7 дней в миллисекундах
        val now = System.currentTimeMillis()
        val weekAgo = now - (7 * 24 * 60 * 60 * 1000L)

        // Получаем все данные и фильтруем
        val allFood = foodRepository.getAllEntries().first()
        val recentFood = allFood.filter { it.timestamp >= weekAgo }

        val allSymptoms = symptomRepository.getAllEntries().first()
        val recentSymptoms = allSymptoms.filter { it.timestamp >= weekAgo }

        val contextParts = mutableListOf<String>()

        if (recentFood.isNotEmpty()) {
            val foodSummary = buildFoodSummary(recentFood)
            contextParts.add("Nutrition during the last week:\n$foodSummary")
        }

        if (recentSymptoms.isNotEmpty()) {
            val symptomsSummary = buildSymptomsSummary(recentSymptoms)
            contextParts.add("Symptoms during the last week:\n$symptomsSummary")
        }

        return contextParts.joinToString("\n\n")
    }

    /**
     * ✅ ИСПРАВЛЕНО: форматирование по timestamp
     */
    private fun buildFoodSummary(entries: List<FoodEntry>): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM")

        return entries
            .groupBy { entry ->
                val date = java.time.Instant.ofEpochMilli(entry.timestamp)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                date
            }
            .entries
            .sortedByDescending { it.key }
            .take(3)
            .joinToString("\n") { (date, dayEntries) ->
                val dateStr = date.format(formatter)
                val foods = dayEntries.joinToString(", ") { it.foodName }
                "- $dateStr: $foods"
            }
    }

    /**
     * ✅ ИСПРАВЛЕНО: форматирование по timestamp
     */
    private fun buildSymptomsSummary(entries: List<SymptomEntry>): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM")

        return entries
            .groupBy { entry ->
                val date = java.time.Instant.ofEpochMilli(entry.timestamp)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                date
            }
            .entries
            .sortedByDescending { it.key }
            .take(3)
            .joinToString("\n") { (date, dayEntries) ->
                val dateStr = date.format(formatter)
                val symptoms = dayEntries.joinToString(", ") {
                    "${it.symptomType.name} (${it.severity.name})"
                }
                "- $dateStr: $symptoms"
            }
    }
}