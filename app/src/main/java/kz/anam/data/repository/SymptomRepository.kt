package kz.anam.data.repository

import kz.anam.data.database.dao.SymptomEntryDao
import kz.anam.data.models.SymptomEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

/**
 * SymptomRepository - handles all symptom entry data operations
 * Critical for AI pattern detection
 */
class SymptomRepository(private val symptomEntryDao: SymptomEntryDao) {

    // Get all symptom entries
    fun getAllEntries(): Flow<List<SymptomEntry>> = symptomEntryDao.getAllEntries()

    // Get entries for specific day
    fun getEntriesForDay(date: LocalDate): Flow<List<SymptomEntry>> {
        val dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return symptomEntryDao.getEntriesForDay(dayStart, dayEnd)
    }

    // Get count for today
    suspend fun getCountForToday(): Int {
        val today = LocalDate.now()
        val dayStart = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val dayEnd = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return symptomEntryDao.getCountForDay(dayStart, dayEnd)
    }

    // Check for critical symptoms today
    suspend fun hasCriticalSymptomsToday(): Boolean {
        val today = LocalDate.now()
        val dayStart = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val dayEnd = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return symptomEntryDao.getCriticalSymptomsCount(dayStart, dayEnd) > 0
    }

    // Add new entry
    suspend fun addEntry(symptomEntry: SymptomEntry): Long {
        return symptomEntryDao.insert(symptomEntry)
    }

    // Update entry
    suspend fun updateEntry(symptomEntry: SymptomEntry) {
        symptomEntryDao.update(symptomEntry)
    }

    // Delete entry
    suspend fun deleteEntry(symptomEntry: SymptomEntry) {
        symptomEntryDao.delete(symptomEntry)
    }

    // Get entries for date range (for AI analysis)
    suspend fun getEntriesInRange(startDate: LocalDate, endDate: LocalDate): List<SymptomEntry> {
        val start = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return symptomEntryDao.getEntriesInRange(start, end)
    }
}