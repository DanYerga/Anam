package kz.anam.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kz.anam.data.repository.*


data class InsightsUiState(
    val triggers: List<FoodTrigger> = emptyList(),
    val riskAssessment: RiskAssessment? = null,
    val aiExplanation: String? = null,
    val isLoading: Boolean = false,
    val isLoadingExplanation: Boolean = false,
    val error: String? = null
)

class AIInsightsViewModel(
    private val insightsRepository: InsightsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadInsights()
    }

    fun loadInsights() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val triggers = insightsRepository.analyzeFoodTriggers()
                val risk = insightsRepository.assessRisk()

                _uiState.value = _uiState.value.copy(
                    triggers = triggers,
                    riskAssessment = risk,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Analysis mistake: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun getAIExplanation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingExplanation = true
            )

            try {
                val result = insightsRepository.getAIExplanation(_uiState.value.triggers)

                result.fold(
                    onSuccess = { explanation ->
                        _uiState.value = _uiState.value.copy(
                            aiExplanation = explanation,
                            isLoadingExplanation = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = "We cannot receive your message: ${exception.message}",
                            isLoadingExplanation = false
                        )
                    }
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Mistake: ${e.message}",
                    isLoadingExplanation = false
                )
            }
        }
    }

    fun dismissExplanation() {
        _uiState.value = _uiState.value.copy(aiExplanation = null)
    }
}