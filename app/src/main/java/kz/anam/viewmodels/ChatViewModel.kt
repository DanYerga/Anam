package kz.anam.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.anam.data.repository.AIChatRepository

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * ChatViewModel - FIXED VERSION
 * Changes:
 * - Removed API key logging (security!)
 * - Fixed error messages (proper English)
 * - Added retry logic for timeout
 * - Cleaner error handling
 */
class ChatViewModel(
    private val aiChatRepository: AIChatRepository
) : ViewModel() {

    companion object {
        private const val MAX_RETRIES = 2
        private const val RETRY_DELAY_MS = 1000L
    }

    private val _messages = MutableStateFlow<List<Message>>(
        listOf(
            Message(
                text = "Hello! I'm ANAM, your AI pregnancy assistant. I can answer your questions about pregnancy and analyze your health data. How can I help you today?",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun sendMessage(text: String, includeContext: Boolean = true) {
        if (text.isBlank()) return

        // ✅ REMOVED: API key logging (security risk!)
        // Never log API keys, even for debugging

        viewModelScope.launch {
            try {
                // Add user message immediately
                _messages.value = _messages.value + Message(text, isUser = true)

                _isLoading.value = true
                _error.value = null

                // Retry logic for timeout issues
                var lastError: String? = null
                var success = false

                repeat(MAX_RETRIES) { attempt ->
                    if (success) return@repeat

                    val result = withContext(Dispatchers.IO) {
                        aiChatRepository.sendMessage(
                            userMessage = text,
                            includeContext = includeContext
                        )
                    }

                    result.fold(
                        onSuccess = { responseText ->
                            _messages.value = _messages.value + Message(
                                text = responseText,
                                isUser = false
                            )
                            success = true
                        },
                        onFailure = { exception ->
                            lastError = getErrorMessage(exception)
                            if (attempt < MAX_RETRIES - 1) {
                                delay(RETRY_DELAY_MS)
                            }
                        }
                    )
                }

                if (!success && lastError != null) {
                    _error.value = lastError
                    _messages.value = _messages.value + Message(
                        text = "Sorry, I couldn't respond. $lastError",
                        isUser = false
                    )
                }

            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
                _messages.value = _messages.value + Message(
                    text = "Something went wrong. Please try again.",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun getErrorMessage(exception: Throwable): String {
        val msg = exception.message ?: ""
        return when {
            msg.contains("Unable to resolve host") ||
                    msg.contains("No address associated") ->
                "No internet connection. Please check your network."

            msg.contains("401") ||
                    msg.contains("403") ->
                "Authentication error. Please contact support."

            msg.contains("timeout", ignoreCase = true) ||
                    msg.contains("SocketTimeout") ->
                "Request timed out. Please try again."

            msg.contains("429") ->
                "Too many requests. Please wait a moment."

            msg.contains("500") ||
                    msg.contains("503") ->
                "Server error. Please try again later."

            else ->
                "Connection error. Please try again."
        }
    }
}