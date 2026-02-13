package kz.anam.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.anam.data.models.FoodCategory
import kz.anam.data.repository.AIChatRepository
import kz.anam.data.repository.FoodRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MealSuggestion(
    val mealType: FoodCategory,
    val dishName: String,
    val description: String,
    val benefits: String,
    val emoji: String,
    val imageUrl: String? = null
)

data class MealPlanUiState(
    val suggestions: List<MealSuggestion> = emptyList(),
    val isGenerating: Boolean = false,
    val isGeneratingImages: Boolean = false,
    val showImagePrompt: Boolean = false,
    val error: String? = null,
    val generatedDate: String = ""
)

/**
 * MealGeneratorViewModel - FIXED VERSION
 * Changes:
 * - New prompt with unique keys (BREAKFAST_NAME, LUNCH_DESC etc.)
 * - Reliable line-by-line parsing instead of extractBetween
 * - Retry logic (3 attempts) for timeout issues
 * - Better error messages
 */
class MealGeneratorViewModel(
    private val aiChatRepository: AIChatRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlanUiState())
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    companion object {
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 1500L
    }

    fun generateMealPlan(pregnancyWeek: Int = 24) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isGenerating = true,
                    error = null,
                    showImagePrompt = false
                )

                val prompt = buildMealPlanPrompt(pregnancyWeek)

                // Retry logic — fixes "not working on first try"
                var lastException: Exception? = null
                var response: String? = null

                repeat(MAX_RETRIES) { attempt ->
                    if (response != null) return@repeat // Already succeeded

                    try {
                        val result = withContext(Dispatchers.IO) {
                            aiChatRepository.sendMessage(
                                userMessage = prompt,
                                includeContext = true
                            )
                        }

                        result.fold(
                            onSuccess = { res ->
                                response = res
                            },
                            onFailure = { ex ->
                                lastException = Exception(ex)
                                if (attempt < MAX_RETRIES - 1) {
                                    delay(RETRY_DELAY_MS)
                                }
                            }
                        )
                    } catch (e: Exception) {
                        lastException = e
                        if (attempt < MAX_RETRIES - 1) {
                            delay(RETRY_DELAY_MS)
                        }
                    }
                }

                if (response != null) {
                    val meals = parseMealPlan(response!!)
                    _uiState.value = _uiState.value.copy(
                        suggestions = meals,
                        isGenerating = false,
                        showImagePrompt = true,
                        generatedDate = getCurrentDate()
                    )
                } else {
                    // All retries failed — use fallback meals
                    _uiState.value = _uiState.value.copy(
                        suggestions = getFallbackMeals(),
                        isGenerating = false,
                        showImagePrompt = false,
                        error = "Connection issue. Showing recommended menu."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    suggestions = getFallbackMeals(),
                    error = "Error: ${e.message}",
                    isGenerating = false
                )
            }
        }
    }

    fun generateImages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGeneratingImages = true,
                showImagePrompt = false
            )

            // TODO: DALL-E integration
            delay(2000)

            _uiState.value = _uiState.value.copy(
                isGeneratingImages = false
            )
        }
    }

    fun skipImages() {
        _uiState.value = _uiState.value.copy(
            showImagePrompt = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun saveMealToTracker(meal: MealSuggestion) {
        viewModelScope.launch {
            try {
                foodRepository.addEntry(
                    foodName = meal.dishName,
                    category = meal.mealType,
                    portionSize = "1 serving"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save: ${e.message}"
                )
            }
        }
    }

    // ─────────────────────────────────────────────
    // PROMPT — unique keys per field, no ambiguity
    // ─────────────────────────────────────────────

    private fun buildMealPlanPrompt(week: Int): String {
        return """
Create a daily menu for a pregnant woman at week $week.

Requirements:
- Avoid foods that previously caused symptoms (if known from context)
- Include nutrients important for week $week
- Keep dishes simple and easy to prepare

Respond ONLY in this exact format, no extra text:

BREAKFAST_NAME: [dish name]
BREAKFAST_DESC: [ingredients, 1 line]
BREAKFAST_BENEFITS: [why beneficial, 1 line]

LUNCH_NAME: [dish name]
LUNCH_DESC: [ingredients, 1 line]
LUNCH_BENEFITS: [why beneficial, 1 line]

DINNER_NAME: [dish name]
DINNER_DESC: [ingredients, 1 line]
DINNER_BENEFITS: [why beneficial, 1 line]
""".trimIndent()
    }

    // ─────────────────────────────────────────────
    // PARSER — simple line-by-line, always reliable
    // ─────────────────────────────────────────────

    private fun parseMealPlan(response: String): List<MealSuggestion> {
        return try {
            val meals = mutableListOf<MealSuggestion>()

            // Extract value by unique key
            fun extractLine(key: String): String {
                return response.lines()
                    .find { it.trim().startsWith(key) }
                    ?.substringAfter(":")
                    ?.trim()
                    ?: ""
            }

            // Breakfast
            val breakfastName = extractLine("BREAKFAST_NAME")
            if (breakfastName.isNotBlank()) {
                meals.add(
                    MealSuggestion(
                        mealType = FoodCategory.BREAKFAST,
                        dishName = breakfastName,
                        description = extractLine("BREAKFAST_DESC"),
                        benefits = extractLine("BREAKFAST_BENEFITS"),
                        emoji = "🌅"
                    )
                )
            }

            // Lunch
            val lunchName = extractLine("LUNCH_NAME")
            if (lunchName.isNotBlank()) {
                meals.add(
                    MealSuggestion(
                        mealType = FoodCategory.LUNCH,
                        dishName = lunchName,
                        description = extractLine("LUNCH_DESC"),
                        benefits = extractLine("LUNCH_BENEFITS"),
                        emoji = "☀️"
                    )
                )
            }

            // Dinner
            val dinnerName = extractLine("DINNER_NAME")
            if (dinnerName.isNotBlank()) {
                meals.add(
                    MealSuggestion(
                        mealType = FoodCategory.DINNER,
                        dishName = dinnerName,
                        description = extractLine("DINNER_DESC"),
                        benefits = extractLine("DINNER_BENEFITS"),
                        emoji = "🌙"
                    )
                )
            }

            // If parsing got nothing — use fallback
            if (meals.isEmpty()) getFallbackMeals() else meals

        } catch (e: Exception) {
            getFallbackMeals()
        }
    }

    // ─────────────────────────────────────────────
    // FALLBACK — shown when API fails completely
    // ─────────────────────────────────────────────

    private fun getFallbackMeals(): List<MealSuggestion> {
        return listOf(
            MealSuggestion(
                mealType = FoodCategory.BREAKFAST,
                dishName = "Oatmeal with Banana and Walnuts",
                description = "Rolled oats, banana, walnuts, honey",
                benefits = "Rich in folic acid, fiber and slow carbs",
                emoji = "🌅"
            ),
            MealSuggestion(
                mealType = FoodCategory.LUNCH,
                dishName = "Steamed Chicken with Vegetables",
                description = "Chicken breast, broccoli, carrots, cauliflower",
                benefits = "Protein for baby's development, B vitamins",
                emoji = "☀️"
            ),
            MealSuggestion(
                mealType = FoodCategory.DINNER,
                dishName = "Baked Salmon with Quinoa",
                description = "Salmon fillet, quinoa, spinach, lemon",
                benefits = "Omega-3 for baby's brain development, iron",
                emoji = "🌙"
            )
        )
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd MMMM", Locale.ENGLISH)
        return formatter.format(Date())
    }
}