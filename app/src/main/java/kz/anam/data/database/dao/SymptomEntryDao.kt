package kz.anam.data.database.dao

import androidx.room.*
import kz.anam.data.models.SymptomEntry
import kz.anam.data.models.SymptomSeverity
import kotlinx.coroutines.flow.Flow

/**
 * SymptomEntryDao - Data Access Object for symptom entries
 * Critical for AI pattern detection
 */
@Dao
interface SymptomEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(symptomEntry: SymptomEntry): Long

    @Update
    suspend fun update(symptomEntry: SymptomEntry)

    @Delete
    suspend fun delete(symptomEntry: SymptomEntry)

    @Query("SELECT * FROM symptom_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<SymptomEntry>>

    @Query("SELECT * FROM symptom_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): SymptomEntry?

    // Get entries for specific date
    @Query("""
        SELECT * FROM symptom_entries 
        WHERE timestamp >= :dayStart AND timestamp < :dayEnd 
        ORDER BY timestamp DESC
    """)
    fun getEntriesForDay(dayStart: Long, dayEnd: Long): Flow<List<SymptomEntry>>

    // Count entries for today
    @Query("""
        SELECT COUNT(*) FROM symptom_entries 
        WHERE timestamp >= :dayStart AND timestamp < :dayEnd
    """)
    suspend fun getCountForDay(dayStart: Long, dayEnd: Long): Int

    // Get entries for date range (for AI analysis)
    @Query("""
        SELECT * FROM symptom_entries 
        WHERE timestamp >= :startDate AND timestamp <= :endDate 
        ORDER BY timestamp ASC
    """)
    suspend fun getEntriesInRange(startDate: Long, endDate: Long): List<SymptomEntry>

    // Check for critical symptoms today
    @Query("""
        SELECT COUNT(*) FROM symptom_entries 
        WHERE timestamp >= :dayStart AND timestamp < :dayEnd 
        AND severity IN ('VERY_SEVERE', 'CRITICAL')
    """)
    suspend fun getCriticalSymptomsCount(dayStart: Long, dayEnd: Long): Int
}