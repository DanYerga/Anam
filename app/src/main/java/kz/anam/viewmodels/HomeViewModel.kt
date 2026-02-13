package kz.anam.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kz.anam.data.models.FoodEntry
import kz.anam.data.models.SymptomEntry
import kz.anam.data.repository.FoodRepository
import kz.anam.data.repository.SymptomRepository

/**
 * ViewModel для главного экрана
 * Путь: app/src/main/java/kz/anam/viewmodels/HomeViewModel.kt
 * ОБНОВЛЁН: добавлены recentFood, recentSymptoms, todayStats
 */

data class TodayStats(
    val foodCount: Int = 0,
    val symptomCount: Int = 0,
    val waterGlasses: Int = 0
)

class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val symptomRepository: SymptomRepository
) : ViewModel() {

    private val _recentFood = MutableStateFlow<List<FoodEntry>>(emptyList())
    val recentFood: StateFlow<List<FoodEntry>> = _recentFood.asStateFlow()

    private val _recentSymptoms = MutableStateFlow<List<SymptomEntry>>(emptyList())
    val recentSymptoms: StateFlow<List<SymptomEntry>> = _recentSymptoms.asStateFlow()

    private val _todayStats = MutableStateFlow(TodayStats())
    val todayStats: StateFlow<TodayStats> = _todayStats.asStateFlow()

    init {
        loadRecentData()
        loadTodayStats()
    }

    private fun loadRecentData() {
        viewModelScope.launch {
            foodRepository.getAllEntries().collect { entries ->
                _recentFood.value = entries.take(5)
            }
        }

        viewModelScope.launch {
            symptomRepository.getAllEntries().collect { entries ->
                _recentSymptoms.value = entries.take(5)
            }
        }
    }

    private fun loadTodayStats() {
        viewModelScope.launch {
            val startOfDay = getStartOfDayTimestamp()

            // Count food entries today
            foodRepository.getAllEntries().collect { allFood ->
                val todayFood = allFood.filter { it.timestamp >= startOfDay }

                // Count symptoms today
                symptomRepository.getAllEntries().collect { allSymptoms ->
                    val todaySymptoms = allSymptoms.filter { it.timestamp >= startOfDay }

                    _todayStats.value = TodayStats(
                        foodCount = todayFood.size,
                        symptomCount = todaySymptoms.size,
                        waterGlasses = 0 // TODO: implement water tracking
                    )
                }
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

    fun refreshData() {
        loadRecentData()
        loadTodayStats()
    }
}