package kz.anam.data.repository

import kz.anam.data.models.*

/**
 * Repository для медиа контента (музыка, видео, подкасты, медитации)
 * Путь: app/src/main/java/kz/anam/data/repository/MediaRepository.kt
 */
class MediaRepository {

    fun getRecommendedContent(pregnancyWeek: Int): List<MediaContent> {
        val trimester = when {
            pregnancyWeek <= 13 -> PregnancyTrimester.FIRST
            pregnancyWeek <= 26 -> PregnancyTrimester.SECOND
            else -> PregnancyTrimester.THIRD
        }
        return getAllContent().filter { trimester in it.recommendedFor }
    }

    fun getContentByType(type: MediaType): List<MediaContent> {
        return getAllContent().filter { it.type == type }
    }

    private fun getAllContent(): List<MediaContent> {
        return listOf(
            // МУЗЫКА ДЛЯ ПЛОДА
            MediaContent(
                id = "music_1",
                title = "Моцарт для малыша",
                description = "Классическая музыка для развития мозга",
                type = MediaType.MUSIC,
                thumbnailUrl = "https://img.youtube.com/vi/jgpJVI3tDbY/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=jgpJVI3tDbY",
                duration = "3:00:00",
                recommendedFor = listOf(PregnancyTrimester.SECOND, PregnancyTrimester.THIRD),
                tags = listOf("классика", "развитие", "спокойствие")
            ),
            MediaContent(
                id = "music_2",
                title = "Звуки природы для беременных",
                description = "Расслабляющие звуки воды и леса",
                type = MediaType.MUSIC,
                thumbnailUrl = "https://img.youtube.com/vi/lE6RYpe9IT0/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=lE6RYpe9IT0",
                duration = "2:00:00",
                recommendedFor = listOf(PregnancyTrimester.FIRST, PregnancyTrimester.SECOND, PregnancyTrimester.THIRD),
                tags = listOf("природа", "релакс", "сон")
            ),
            MediaContent(
                id = "music_3",
                title = "Колыбельные для плода",
                description = "Нежные мелодии для связи с малышом",
                type = MediaType.MUSIC,
                thumbnailUrl = "https://img.youtube.com/vi/eKFTSSKCzWA/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=eKFTSSKCzWA",
                duration = "1:30:00",
                recommendedFor = listOf(PregnancyTrimester.SECOND, PregnancyTrimester.THIRD),
                tags = listOf("колыбельные", "связь", "любовь")
            ),

            // МЕДИТАЦИИ ДЛЯ МАМЫ
            MediaContent(
                id = "meditation_1",
                title = "Медитация для беременных: Первый триместр",
                description = "Справляемся с токсикозом и тревогой",
                type = MediaType.MEDITATION,
                thumbnailUrl = "https://img.youtube.com/vi/5jU8GN3xYGk/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=5jU8GN3xYGk",
                duration = "15:00",
                recommendedFor = listOf(PregnancyTrimester.FIRST),
                tags = listOf("токсикоз", "тревога", "принятие")
            ),
            MediaContent(
                id = "meditation_2",
                title = "Дыхание для родов",
                description = "Практика дыхательных техник",
                type = MediaType.MEDITATION,
                thumbnailUrl = "https://img.youtube.com/vi/2cZZXm3Zvw0/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=2cZZXm3Zvw0",
                duration = "12:30",
                recommendedFor = listOf(PregnancyTrimester.THIRD),
                tags = listOf("дыхание", "роды", "подготовка")
            ),
            MediaContent(
                id = "meditation_3",
                title = "Снятие стресса во время беременности",
                description = "Расслабление и позитивный настрой",
                type = MediaType.MEDITATION,
                thumbnailUrl = "https://img.youtube.com/vi/O-6f5wQXSu8/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=O-6f5wQXSu8",
                duration = "20:00",
                recommendedFor = listOf(PregnancyTrimester.FIRST, PregnancyTrimester.SECOND, PregnancyTrimester.THIRD),
                tags = listOf("стресс", "релакс", "гармония")
            ),

            // ВИДЕО О БЕРЕМЕННОСТИ
            MediaContent(
                id = "video_1",
                title = "Развитие ребёнка по неделям",
                description = "Что происходит с малышом каждую неделю",
                type = MediaType.VIDEO,
                thumbnailUrl = "https://img.youtube.com/vi/TZXfiLAHMvc/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=TZXfiLAHMvc",
                duration = "10:45",
                recommendedFor = listOf(PregnancyTrimester.FIRST, PregnancyTrimester.SECOND, PregnancyTrimester.THIRD),
                tags = listOf("развитие", "недели", "образование")
            ),
            MediaContent(
                id = "video_2",
                title = "Йога для беременных",
                description = "Безопасные упражнения по триместрам",
                type = MediaType.VIDEO,
                thumbnailUrl = "https://img.youtube.com/vi/XjebLr5I4IE/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=XjebLr5I4IE",
                duration = "25:00",
                recommendedFor = listOf(PregnancyTrimester.SECOND, PregnancyTrimester.THIRD),
                tags = listOf("йога", "упражнения", "здоровье")
            ),
            MediaContent(
                id = "video_3",
                title = "Питание во время беременности",
                description = "Что можно и нельзя есть",
                type = MediaType.VIDEO,
                thumbnailUrl = "https://img.youtube.com/vi/uO3bXaQGRFQ/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=uO3bXaQGRFQ",
                duration = "18:20",
                recommendedFor = listOf(PregnancyTrimester.FIRST, PregnancyTrimester.SECOND, PregnancyTrimester.THIRD),
                tags = listOf("питание", "еда", "здоровье")
            ),

            // ПОДКАСТЫ
            MediaContent(
                id = "podcast_1",
                title = "Подкаст: Токсикоз — как пережить?",
                description = "Реальные истории и советы врачей",
                type = MediaType.PODCAST,
                thumbnailUrl = "https://img.youtube.com/vi/b8UFOfRM1tY/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=b8UFOfRM1tY",
                duration = "35:00",
                recommendedFor = listOf(PregnancyTrimester.FIRST),
                tags = listOf("токсикоз", "советы", "опыт")
            ),
            MediaContent(
                id = "podcast_2",
                title = "Психология беременности",
                description = "Как справиться с эмоциями",
                type = MediaType.PODCAST,
                thumbnailUrl = "https://img.youtube.com/vi/GbLYZzZ1vJc/maxresdefault.jpg",
                contentUrl = "https://www.youtube.com/watch?v=GbLYZzZ1vJc",
                duration = "42:15",
                recommendedFor = listOf(PregnancyTrimester.FIRST, PregnancyTrimester.SECOND, PregnancyTrimester.THIRD),
                tags = listOf("психология", "эмоции", "поддержка")
            )
        )
    }
}