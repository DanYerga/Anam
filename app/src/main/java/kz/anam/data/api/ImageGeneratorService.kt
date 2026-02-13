package kz.anam.data.api

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * DALL-E API Service для генерации изображений блюд
 * Путь: app/src/main/java/kz/anam/data/api/ImageGeneratorService.kt
 */
interface ImageGeneratorService {

    @POST("v1/images/generations")
    suspend fun generateImage(
        @Header("Authorization") authorization: String,
        @Body request: ImageGenerationRequest
    ): ImageGenerationResponse

    companion object {
        private const val BASE_URL = "https://api.openai.com/"

        fun create(): ImageGeneratorService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ImageGeneratorService::class.java)
        }
    }
}

// Request
data class ImageGenerationRequest(
    val model: String = "dall-e-3",
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1024",
    val quality: String = "standard"
)

// Response
data class ImageGenerationResponse(
    val created: Long,
    val data: List<GeneratedImage>
)

data class GeneratedImage(
    val url: String,
    @SerializedName("revised_prompt") val revisedPrompt: String?
)