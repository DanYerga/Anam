package kz.anam.data.repository

import kz.anam.data.api.ImageGenerationRequest
import kz.anam.data.api.ImageGeneratorService

/**
 * Repository для генерации изображений через DALL-E
 * Путь: app/src/main/java/kz/anam/data/repository/ImageGeneratorRepository.kt
 */
class ImageGeneratorRepository(
    private val imageGeneratorService: ImageGeneratorService,
    private val apiKey: String
) {

    /**
     * Генерирует изображение блюда
     */
    suspend fun generateFoodImage(dishName: String, description: String): Result<String> {
        return try {
            val prompt = buildFoodPrompt(dishName, description)

            val response = imageGeneratorService.generateImage(
                authorization = "Bearer $apiKey",
                request = ImageGenerationRequest(
                    prompt = prompt,
                    size = "1024x1024",
                    quality = "standard"
                )
            )

            val imageUrl = response.data.firstOrNull()?.url

            if (imageUrl != null) {
                Result.success(imageUrl)
            } else {
                Result.failure(Exception("No image generated"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Строит промпт для DALL-E
     */
    private fun buildFoodPrompt(dishName: String, description: String): String {
        return """
A professional, appetizing food photography of $dishName.
The dish contains: $description.
Shot from above on a white ceramic plate, natural lighting, minimalist style.
High quality, realistic, Instagram-worthy food photo.
        """.trimIndent()
    }
}