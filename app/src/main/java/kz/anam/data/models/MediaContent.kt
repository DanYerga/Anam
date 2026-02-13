package kz.anam.data.models

/**
 * Модели для медиа контента (музыка, видео, подкасты)
 * Путь: app/src/main/java/kz/anam/data/models/MediaContent.kt
 */

enum class MediaType {
    MUSIC,
    VIDEO,
    PODCAST,
    MEDITATION
}

enum class PregnancyTrimester {
    FIRST,    // 1-13 недель
    SECOND,   // 14-26 недель
    THIRD     // 27-40 недель
}

data class MediaContent(
    val id: String,
    val title: String,
    val description: String,
    val type: MediaType,
    val thumbnailUrl: String,
    val contentUrl: String,  // YouTube/Spotify URL
    val duration: String,     // "5:30"
    val recommendedFor: List<PregnancyTrimester>,
    val tags: List<String>
)