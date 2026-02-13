package kz.anam.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kz.anam.data.models.SymptomEntry
import kz.anam.data.models.SymptomSeverity
import kz.anam.data.models.SymptomType
import kz.anam.data.repository.SymptomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * SymptomLogViewModel - business logic for symptom logging
 */
class SymptomLogViewModel(
    private val symptomRepository: SymptomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SymptomLogUiState())
    val uiState: StateFlow<SymptomLogUiState> = _uiState.asStateFlow()

    init {
        loadTodayEntries()
    }

    private fun loadTodayEntries() {
        viewModelScope.launch {
            symptomRepository.getEntriesForDay(LocalDate.now()).collect { entries ->
                _uiState.value = _uiState.value.copy(
                    todayEntries = entries,
                    isLoading = false
                )
            }
        }
    }

    fun addSymptomEntry(
        symptomType: SymptomType,
        severity: SymptomSeverity,
        duration: Int = 0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            try {
                val entry = SymptomEntry(
                    symptomType = symptomType,
                    severity = severity,
                    timestamp = System.currentTimeMillis(),
                    duration = duration,
                    notes = notes
                )

                symptomRepository.addEntry(entry)

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )

                // Reset success flag
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(saveSuccess = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteEntry(entry: SymptomEntry) {
        viewModelScope.launch {
            try {
                symptomRepository.deleteEntry(entry)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SymptomLogUiState(
    val todayEntries: List<SymptomEntry> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)