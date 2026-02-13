package kz.anam.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kz.anam.data.database.dao.FoodEntryDao
import kz.anam.data.database.dao.SymptomEntryDao
import kz.anam.data.database.dao.DocumentDao
import kz.anam.data.models.FoodEntry
import kz.anam.data.models.SymptomEntry
import kz.anam.data.models.Document

/**
 * AnamDatabase - main Room database
 * Single source of truth for all app data
 */
@Database(
    entities = [
        FoodEntry::class,
        SymptomEntry::class,
        Document::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AnamDatabase : RoomDatabase() {

    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun symptomEntryDao(): SymptomEntryDao
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile
        private var INSTANCE: AnamDatabase? = null

        fun getDatabase(context: Context): AnamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnamDatabase::class.java,
                    "anam_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}