package kz.anam.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * FoodEntry - core data model for food logging
 * Each entry represents one food item consumed
 */
@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val foodName: String,           // e.g. "Яблоко", "Курица", "Молоко"
    val category: FoodCategory,      // Breakfast, Lunch, Dinner, Snack
    val timestamp: Long,             // Unix timestamp
    val portionSize: String = "",    // e.g. "1 шт", "200г"
    val notes: String = "",          // User notes

    // For AI pattern detection
    val tags: List<String> = emptyList()  // e.g. ["dairy", "protein", "citrus"]
)

enum class FoodCategory {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}

// Extension to get display name
fun FoodCategory.displayName(): String = when(this) {
    FoodCategory.BREAKFAST -> "Breakfast"
    FoodCategory.LUNCH -> "Lunch"
    FoodCategory.DINNER -> "Dinner"
    FoodCategory.SNACK -> "Snack"
}