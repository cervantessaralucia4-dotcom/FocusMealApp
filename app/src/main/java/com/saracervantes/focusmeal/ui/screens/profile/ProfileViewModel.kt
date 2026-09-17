package com.saracervantes.focusmeal.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.model.User
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.UserRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class ProfileUiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val user: User? = null,
        val name: String = "",
        val email: String = "",
        val age: String = "",
        val gender: String = "",
        val weight: String = "",
        val height: String = "",
        val goal: String = "",
        val dietType: String = "",
        val isEditing: Boolean = false
    )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = authRepository.currentUser?.uid ?: ""
            if (userId.isBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "No hay sesión activa") }
                return@launch
            }
            when (val result = userRepository.getUser(userId)) {
                is Resource.Success -> {
                    val user = result.data ?: return@launch
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            name = user.name,
                            email = user.email,
                            age = if (user.age > 0) user.age.toString() else "",
                            gender = user.gender,
                            weight = if (user.weight > 0) user.weight.toString() else "",
                            height = if (user.height > 0) user.height.toString() else "",
                            goal = user.goal,
                            dietType = user.dietType,
                            isEditing = false
                        )
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.update { current ->
            when (field) {
                "name" -> current.copy(name = value)
                "age" -> current.copy(age = value)
                "gender" -> current.copy(gender = value)
                "weight" -> current.copy(weight = value)
                "height" -> current.copy(height = value)
                "goal" -> current.copy(goal = value)
                "dietType" -> current.copy(dietType = value)
                else -> current
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val current = _uiState.value
            val user = current.user ?: return@launch
            val updatedUser = user.copy(
                name = current.name,
                age = current.age.toIntOrNull() ?: 0,
                gender = current.gender,
                weight = current.weight.toFloatOrNull() ?: 0f,
                height = current.height.toFloatOrNull() ?: 0f,
                goal = current.goal,
                dietType = current.dietType
            )
            _uiState.update { it.copy(isEditing = true, error = null) }
            when (val result = userRepository.updateUser(updatedUser)) {
                is Resource.Success -> _uiState.update { it.copy(isEditing = false) }
                is Resource.Error -> _uiState.update { it.copy(isEditing = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun toggleEdit() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }
}
