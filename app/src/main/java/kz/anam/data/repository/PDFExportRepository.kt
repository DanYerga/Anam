package kz.anam.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kz.anam.data.models.FoodEntry
import kz.anam.data.models.SymptomEntry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository для экспорта данных в PDF/CSV
 * Путь: app/src/main/java/kz/anam/data/repository/PDFExportRepository.kt
 *
 * СОВМЕСТИМ с timestamp: Long моделями
 */
class PDFExportRepository(
    private val context: Context,
    private val foodRepository: FoodRepository,
    private val symptomRepository: SymptomRepository
) {

    /**
     * Генерирует текстовый отчёт для врача
     * TODO: В будущем заменить на PDF с библиотекой iText
     */
    suspend fun generateDoctorReport(
        daysBack: Int = 30
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000L)

            // Получаем данные
            val allFood = foodRepository.getAllEntries().first()
                .filter { it.timestamp >= startTime }

            val allSymptoms = symptomRepository.getAllEntries().first()
                .filter { it.timestamp >= startTime }

            // Создаём файл
            val fileName = "anam_report_${System.currentTimeMillis()}.txt"
            val file = File(context.getExternalFilesDir(null), fileName)

            // Генерируем содержимое
            generateReportContent(file, allFood, allSymptoms, daysBack)

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Генерирует содержимое отчёта
     */
    private fun generateReportContent(
        file: File,
        foodEntries: List<FoodEntry>,
        symptomEntries: List<SymptomEntry>,
        daysBack: Int
    ) {
        FileOutputStream(file).use { output ->
            val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
            val dayFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))

            val content = buildString {
                appendLine("═══════════════════════════════════════")
                appendLine("          МЕДИЦИНСКИЙ ОТЧЁТ")
                appendLine("         Приложение ANAM")
                appendLine("═══════════════════════════════════════")
                appendLine()
                appendLine("Период: Последние $daysBack дней")
                appendLine("Дата создания: ${dateFormatter.format(Date())}")
                appendLine()

                appendLine("─────────────────────────────────────")
                appendLine("ПИТАНИЕ (${foodEntries.size} записей)")
                appendLine("─────────────────────────────────────")

                // Группируем по дням
                val foodByDay = foodEntries
                    .groupBy { dayFormatter.format(Date(it.timestamp)) }
                    .toSortedMap(compareByDescending { it })

                foodByDay.forEach { (day, entries) ->
                    appendLine()
                    appendLine("📅 $day")
                    entries.sortedBy { it.timestamp }.forEach { entry ->
                        val time = dateFormatter.format(Date(entry.timestamp))
                        appendLine("  $time - ${entry.foodName} (${entry.category.name})")
                        if (entry.notes.isNotBlank()) {
                            appendLine("    Заметки: ${entry.notes}")
                        }
                    }
                }

                appendLine()
                appendLine("─────────────────────────────────────")
                appendLine("СИМПТОМЫ (${symptomEntries.size} записей)")
                appendLine("─────────────────────────────────────")

                val symptomsByDay = symptomEntries
                    .groupBy { dayFormatter.format(Date(it.timestamp)) }
                    .toSortedMap(compareByDescending { it })

                symptomsByDay.forEach { (day, entries) ->
                    appendLine()
                    appendLine("📅 $day")
                    entries.sortedBy { it.timestamp }.forEach { entry ->
                        val time = dateFormatter.format(Date(entry.timestamp))
                        appendLine("  $time - ${entry.symptomType.name} (${entry.severity.name})")
                        if (entry.notes.isNotBlank()) {
                            appendLine("    Заметки: ${entry.notes}")
                        }
                    }
                }

                appendLine()
                appendLine("═══════════════════════════════════════")
                appendLine("Создано с помощью ANAM")
                appendLine("AI-помощник для беременных")
                appendLine("═══════════════════════════════════════")
            }

            output.write(content.toByteArray())
        }
    }

    /**
     * Экспорт питания в CSV
     */
    suspend fun exportFoodToCSV(
        daysBack: Int = 30
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000L)

            val foodEntries = foodRepository.getAllEntries().first()
                .filter { it.timestamp >= startTime }

            val fileName = "anam_food_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)

            FileOutputStream(file).use { output ->
                val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))

                val csv = buildString {
                    appendLine("Дата и время,Продукт,Категория,Порция,Заметки")
                    foodEntries.sortedByDescending { it.timestamp }.forEach { entry ->
                        val time = dateFormatter.format(Date(entry.timestamp))
                        appendLine("$time,${entry.foodName},${entry.category.name},${entry.portionSize},\"${entry.notes}\"")
                    }
                }
                output.write(csv.toByteArray())
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Экспорт симптомов в CSV
     */
    suspend fun exportSymptomsToCSV(
        daysBack: Int = 30
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000L)

            val symptomEntries = symptomRepository.getAllEntries().first()
                .filter { it.timestamp >= startTime }

            val fileName = "anam_symptoms_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)

            FileOutputStream(file).use { output ->
                val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))

                val csv = buildString {
                    appendLine("Дата и время,Симптом,Тяжесть,Заметки")
                    symptomEntries.sortedByDescending { it.timestamp }.forEach { entry ->
                        val time = dateFormatter.format(Date(entry.timestamp))
                        appendLine("$time,${entry.symptomType.name},${entry.severity.name},\"${entry.notes}\"")
                    }
                }
                output.write(csv.toByteArray())
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}