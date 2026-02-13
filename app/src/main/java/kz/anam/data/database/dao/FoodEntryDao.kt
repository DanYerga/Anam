package kz.anam.data.database.dao

import androidx.room.*
import kz.anam.data.models.FoodEntry
import kotlinx.coroutines.flow.Flow

/**
 * FoodEntryDao - Data Access Object for food entries
 * All database operations for food logging
 */
@Dao
interface FoodEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodEntry: FoodEntry): Long

    @Update
    suspend fun update(foodEntry: FoodEntry)

    @Delete
    suspend fun delete(foodEntry: FoodEntry)

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): FoodEntry?

    // Get entries for specific date (day start to day end)
    @Query("""
        SELECT * FROM food_entries 
        WHERE timestamp >= :dayStart AND timestamp < :dayEnd 
        ORDER BY timestamp DESC
    """)
    fun getEntriesForDay(dayStart: Long, dayEnd: Long): Flow<List<FoodEntry>>

    // Count entries for today
    @Query("""
        SELECT COUNT(*) FROM food_entries 
        WHERE timestamp >= :dayStart AND timestamp < :dayEnd
    """)
    suspend fun getCountForDay(dayStart: Long, dayEnd: Long): Int

    // Get entries for date range (for AI analysis)
    @Query("""
        SELECT * FROM food_entries 
        WHERE timestamp >= :startDate AND timestamp <= :endDate 
        ORDER BY timestamp ASC
    """)
    suspend fun getEntriesInRange(startDate: Long, endDate: Long): List<FoodEntry>
}