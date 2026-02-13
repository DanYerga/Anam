package kz.anam

/**
 * API Configuration
 *
 * ВАЖНО: НЕ ХРАНИТЕ API КЛЮЧИ В КОДЕ!
 *
 * ПРАВИЛЬНЫЙ СПОСОБ:
 * 1. Создайте файл local.properties в корне проекта
 * 2. Добавьте строку: CLAUDE_API_KEY=ваш_ключ_здесь
 * 3. Файл local.properties уже в .gitignore (не попадёт в Git)
 *
 * Для production используйте:
 * - Backend API (ключ на сервере, не в приложении)
 * - Или Android Secrets Gradle Plugin
 */
object ApiConfig {

    // API Key будет загружен из BuildConfig
    // (см. инструкцию ниже как настроить)
    const val CLAUDE_API_KEY = BuildConfig.CLAUDE_API_KEY

    // Для тестов можно временно вставить ключ сюда:
    // const val CLAUDE_API_KEY = "sk-ant-..."
    // НО УДАЛИТЕ ПЕРЕД COMMIT В GIT!
}