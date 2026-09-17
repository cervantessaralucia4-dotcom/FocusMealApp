package com.saracervantes.focusmeal.ui.screens.addmeal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.model.Meal
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.MealRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddMealViewModel(
    private val mealRepository: MealRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class AddMealUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
        val name: String = "",
        val category: String = "",
        val calories: String = "",
        val proteins: String = "",
        val carbs: String = "",
        val fats: String = "",
        val date: Long = System.currentTimeMillis()
    )

    private val _uiState = MutableStateFlow(AddMealUiState())
    val uiState: StateFlow<AddMealUiState> = _uiState.asStateFlow()

    fun updateField(field: String, value: String) {
        _uiState.update { current ->
            when (field) {
                "name" -> current.copy(name = value)
                "category" -> current.copy(category = value)
                "calories" -> current.copy(calories = value)
                "proteins" -> current.copy(proteins = value)
                "carbs" -> current.copy(carbs = value)
                "fats" -> current.copy(fats = value)
                else -> current
            }
        }
    }

    fun addMeal() {
        viewModelScope.launch {
            val current = _uiState.value
            val userId = authRepository.currentUser?.uid ?: ""
            if (userId.isBlank()) {
                _uiState.update { it.copy(error = "No hay sesión activa") }
                return@launch
            }
            val calories = current.calories.toIntOrNull() ?: 0
            val proteins = current.proteins.toFloatOrNull() ?: 0f
            val carbs = current.carbs.toFloatOrNull() ?: 0f
            val fats = current.fats.toFloatOrNull() ?: 0f

            val meal = Meal(
                userId = userId,
                name = current.name,
                category = current.category,
                calories = calories,
                proteins = proteins,
                carbs = carbs,
                fats = fats,
                date = current.date
            )
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }
            when (val result = mealRepository.addMeal(meal)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, success = true) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(success = false) }
    }
}
