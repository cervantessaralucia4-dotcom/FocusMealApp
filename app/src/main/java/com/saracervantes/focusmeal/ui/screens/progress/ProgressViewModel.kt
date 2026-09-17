package com.saracervantes.focusmeal.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.model.Progress
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.ProgressRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class ProgressUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val records: List<Progress> = emptyList(),
        val weight: String = "",
        val caloriesConsumed: String = ""
    )

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgress()
    }

    fun loadProgress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = authRepository.currentUser?.uid ?: ""
            if (userId.isBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "No hay sesión activa") }
                return@launch
            }
            when (val result = progressRepository.getUserProgress(userId)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, records = result.data ?: emptyList()) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.update { current ->
            when (field) {
                "weight" -> current.copy(weight = value)
                "caloriesConsumed" -> current.copy(caloriesConsumed = value)
                else -> current
            }
        }
    }

    fun addRecord() {
        viewModelScope.launch {
            val current = _uiState.value
            val weight = current.weight.toFloatOrNull() ?: 0f
            val calories = current.caloriesConsumed.toIntOrNull() ?: 0
            val userId = authRepository.currentUser?.uid ?: ""
            if (userId.isBlank()) {
                _uiState.update { it.copy(error = "No hay sesión activa") }
                return@launch
            }
            val record = Progress(
                userId = userId,
                weight = weight,
                caloriesConsumed = calories
            )
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = progressRepository.addProgressRecord(record)) {
                is Resource.Success -> {
                    loadProgress()
                    _uiState.update { it.copy(weight = "", caloriesConsumed = "") }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }
}
