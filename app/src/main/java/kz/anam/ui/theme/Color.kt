package kz.anam.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 🎨 ПОЛНАЯ ЦВЕТОВАЯ ПАЛИТРА
 * Новые современные цвета + все старые для совместимости
 */

// ==========================================
// НОВЫЕ СОВРЕМЕННЫЕ ЦВЕТА
// ==========================================

// Primary Purple Shades - Основные фиолетовые
val SoftLavender = Color(0xFFE6E6FA)        // Нежно-лавандовый для фона
val LightPurple = Color(0xFFD8BFD8)         // Светло-сиреневый
val MediumPurple = Color(0xFFBA68C8)        // Средний фиолетовый
val DeepPurple = Color(0xFF9C27B0)          // Глубокий фиолетовый
val RichPurple = Color(0xFF7B1FA2)          // Насыщенный фиолетовый
val DarkPurple = Color(0xFF4A148C)          // Тёмный фиолетовый

// Accent Colors - Акцентные цвета
val PeachPink = Color(0xFFFFB3BA)           // Нежно-персиковый розовый
val SoftPeach = Color(0xFFFFDAB9)           // Мягкий персиковый
val LightCoral = Color(0xFFFFB6C1)          // Светло-коралловый
val MintGreen = Color(0xFFB5EAD7)           // Мятный зелёный

// Status Colors - Статусы
val Success = Color(0xFF4CAF50)             // Зелёный успех
val Warning = Color(0xFFFFC107)             // Жёлтый предупреждение
val Inactive = Color(0xFF9E9E9E)            // Серый неактивный

// Gradient Colors - Для градиентов
val GradientPurpleStart = Color(0xFFE1BEE7) // Светлый старт
val GradientPurpleEnd = Color(0xFFBA68C8)   // Средний конец
val GradientPeachStart = Color(0xFFFFF0F5)  // Очень светлый персик
val GradientPeachEnd = Color(0xFFFFB3BA)    // Нежный розовый

// Neutral Colors - Нейтральные
val PureWhite = Color(0xFFFFFFFF)
val OffWhite = Color(0xFFFAF9FC)            // Слегка фиолетовый белый
val LightGray = Color(0xFFF5F5F5)
val MediumGray = Color(0xFFBDBDBD)
val DarkGray = Color(0xFF616161)

// Text Colors - Текст
val TextPrimary = Color(0xFF2D2D2D)
val TextSecondary = Color(0xFF757575)
val TextTertiary = Color(0xFF9E9E9E)        // Третичный текст (неактивный)
val TextOnPurple = Color(0xFFFFFFFF)

// Glassmorphism Colors - Для эффекта стекла
val GlassWhite = Color(0xCCFFFFFF)          // Полупрозрачный белый
val GlassPurple = Color(0x99E1BEE7)         // Полупрозрачный фиолетовый
val GlassBorder = Color(0x33FFFFFF)         // Тонкая белая граница

// ==========================================
// СТАРЫЕ ЦВЕТА - для обратной совместимости
// (Алиасы на новые цвета)
// ==========================================
val RoyalViolet = MediumPurple              // Было: Color(0xFF7B2CBF)
val DeepEggplant = DeepPurple               // Было: Color(0xFF5A189A)
val ElectricViolet = LightPurple            // Было: Color(0xFF9D4EDD)
val SoftViolet = SoftLavender               // Было: Color(0xFFE0AAFF)
val NightPlum = DarkPurple                  // Было: Color(0xFF3C096C)

// Поверхности и границы
val LightSurface = OffWhite                 // Было: Color(0xFFF8F9FA)
val LightBorder = LightGray                 // Было: Color(0xFFE9ECEF)

// Ошибки
val Error = LightCoral                      // Было: Color(0xFFDC3545)