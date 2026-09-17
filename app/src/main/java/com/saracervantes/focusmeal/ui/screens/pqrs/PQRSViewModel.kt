package com.saracervantes.focusmeal.ui.screens.pqrs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.model.PQRS
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.PQRSRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PQRSViewModel(
    private val pqrsRepository: PQRSRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class PQRSUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val records: List<PQRS> = emptyList(),
        val type: String = "",
        val subject: String = "",
        val message: String = "",
        val status: String = "Open"
    )

    private val _uiState = MutableStateFlow(PQRSUiState())
    val uiState: StateFlow<PQRSUiState> = _uiState.asStateFlow()

    init {
        loadRecords()
    }

    fun loadRecords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = authRepository.currentUser?.uid ?: ""
            if (userId.isBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "No hay sesión activa") }
                return@launch
            }
            when (val result = pqrsRepository.getUserPQRS(userId)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, records = result.data ?: emptyList()) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.update { current ->
            when (field) {
                "type" -> current.copy(type = value)
                "subject" -> current.copy(subject = value)
                "message" -> current.copy(message = value)
                "status" -> current.copy(status = value)
                else -> current
            }
        }
    }

    fun submitPQRS() {
        viewModelScope.launch {
            val current = _uiState.value
            if (current.type.isBlank() || current.subject.isBlank() || current.message.isBlank()) {
                _uiState.update { it.copy(error = "Completa todos los campos") }
                return@launch
            }
            val userId = authRepository.currentUser?.uid ?: ""
            val record = PQRS(
                userId = userId,
                type = current.type,
                subject = current.subject,
                message = current.message,
                status = current.status
            )
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = pqrsRepository.submitPQRS(record)) {
                is Resource.Success -> {
                    loadRecords()
                    _uiState.update { it.copy(type = "", subject = "", message = "") }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateStatus(pqrsId: String, status: String) {
        viewModelScope.launch {
            when (val result = pqrsRepository.updateStatus(pqrsId, status)) {
                is Resource.Success -> loadRecords()
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }
}
