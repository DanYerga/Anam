package kz.anam.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kz.anam.data.models.FoodCategory
import kz.anam.data.models.FoodEntry
import kz.anam.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * FoodLogViewModel - РЕАЛЬНО ФИНАЛЬНАЯ ВЕРСИЯ
 * Путь: app/src/main/java/kz/anam/viewmodels/FoodLogViewModel.kt
 * ИСПРАВЛЕНО: использует отдельные параметры для addEntry
 */
class FoodLogViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodLogUiState())
    val uiState: StateFlow<FoodLogUiState> = _uiState.asStateFlow()

    init {
        loadTodayEntries()
    }

    private fun loadTodayEntries() {
        viewModelScope.launch {
            foodRepository.getAllEntries().collect { entries ->
                val today = getStartOfDayTimestamp()
                val todayEntries = entries.filter { it.timestamp >= today }

                _uiState.value = _uiState.value.copy(
                    todayEntries = todayEntries,
                    isLoading = false
                )
            }
        }
    }

    private fun getStartOfDayTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun addFoodEntry(
        foodName: String,
        category: FoodCategory,
        portionSize: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            try {
                // ✅ ИСПРАВЛЕНО: передаём отдельные параметры!
                foodRepository.addEntry(
                    foodName = foodName,
                    category = category,
                    portionSize = portionSize
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )

                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(saveSuccess = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch {
            try {
                foodRepository.deleteEntry(entry)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class FoodLogUiState(
    val todayEntries: List<FoodEntry> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)