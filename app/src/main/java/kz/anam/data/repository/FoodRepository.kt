package kz.anam.data.repository

import kz.anam.data.database.dao.FoodEntryDao
import kz.anam.data.models.FoodEntry
import kotlinx.coroutines.flow.Flow
import kz.anam.data.models.FoodCategory

/**
 * FoodRepository - ИСПРАВЛЕННАЯ ВЕРСИЯ
 * Путь: app/src/main/java/kz/anam/data/repository/FoodRepository.kt
 */
class FoodRepository(private val foodEntryDao: FoodEntryDao) {

    fun getAllEntries(): Flow<List<FoodEntry>> = foodEntryDao.getAllEntries()

    // Get entries for today (using timestamp)
    fun getEntriesForToday(): Flow<List<FoodEntry>> {
        val dayStart = getStartOfDayTimestamp()
        val dayEnd = dayStart + (24 * 60 * 60 * 1000)
        return foodEntryDao.getEntriesForDay(dayStart, dayEnd)
    }

    private fun getStartOfDayTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    suspend fun getCountForToday(): Int {
        val dayStart = getStartOfDayTimestamp()
        val dayEnd = dayStart + (24 * 60 * 60 * 1000)
        return foodEntryDao.getCountForDay(dayStart, dayEnd)
    }

    // ✅ ИСПРАВЛЕНО: только объект FoodEntry!
    suspend fun addEntry(foodEntry: FoodEntry): Long {
        return foodEntryDao.insert(foodEntry)
    }

    // ✅ УДОБНЫЙ МЕТОД: создаёт FoodEntry автоматически
    suspend fun addEntry(
        foodName: String,
        category: FoodCategory,
        portionSize: String = "",
        notes: String = ""
    ): Long {
        val entry = FoodEntry(
            id = 0,
            foodName = foodName,
            category = category,
            timestamp = System.currentTimeMillis(),
            portionSize = portionSize,
            notes = notes,
            tags = emptyList()
        )
        return foodEntryDao.insert(entry)
    }

    suspend fun updateEntry(foodEntry: FoodEntry) {
        foodEntryDao.update(foodEntry)
    }

    suspend fun deleteEntry(foodEntry: FoodEntry) {
        foodEntryDao.delete(foodEntry)
    }

    // Get entries in timestamp range
    suspend fun getEntriesInRange(startTimestamp: Long, endTimestamp: Long): List<FoodEntry> {
        return foodEntryDao.getEntriesInRange(startTimestamp, endTimestamp)
    }
}