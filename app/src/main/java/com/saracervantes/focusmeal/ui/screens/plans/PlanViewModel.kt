package com.saracervantes.focusmeal.ui.screens.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.model.Plan
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.PlanRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlanViewModel(
    private val planRepository: PlanRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class PlanUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val plans: List<Plan> = emptyList(),
        val name: String = "",
        val description: String = "",
        val caloriesPerDay: String = "",
        val dietType: String = ""
    )

    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = planRepository.getPlans()) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, plans = result.data ?: emptyList()) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.update { current ->
            when (field) {
                "name" -> current.copy(name = value)
                "description" -> current.copy(description = value)
                "caloriesPerDay" -> current.copy(caloriesPerDay = value)
                "dietType" -> current.copy(dietType = value)
                else -> current
            }
        }
    }

    fun addPlan() {
        viewModelScope.launch {
            val current = _uiState.value
            val calories = current.caloriesPerDay.toIntOrNull() ?: 0
            val userId = authRepository.currentUser?.uid ?: ""
            if (userId.isBlank()) {
                _uiState.update { it.copy(error = "No hay sesión activa") }
                return@launch
            }
            val plan = Plan(
                name = current.name,
                description = current.description,
                caloriesPerDay = calories,
                dietType = current.dietType
            )
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = planRepository.addPlan(plan)) {
                is Resource.Success -> {
                    loadPlans()
                    _uiState.update { it.copy(name = "", description = "", caloriesPerDay = "", dietType = "") }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }
    private val aiRepository = com.saracervantes.focusmeal.data.repository.AiRepository()
    private val userRepository = com.saracervantes.focusmeal.data.repository.UserRepository(com.google.firebase.firestore.FirebaseFirestore.getInstance())

    fun generateAIPlan() {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Get user profile
            val userResult = userRepository.getUser(userId)
            if (userResult is Resource.Success && userResult.data != null) {
                val user = userResult.data
                // Ask Gemini to generate the plan
                val aiResult = aiRepository.generateDietPlan(
                    weight = user.weight,
                    age = user.age,
                    goal = user.goal,
                    dietType = user.dietType
                )
                
                when (aiResult) {
                    is Resource.Success -> {
                        val planData = aiResult.data
                        if (planData != null) {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    name = planData.name,
                                    description = planData.description,
                                    caloriesPerDay = planData.caloriesPerDay.toString(),
                                    dietType = user.dietType // Autofill user's preferred diet
                                )
                            }
                        }
                    }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = aiResult.message) }
                    is Resource.Loading -> Unit
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo obtener el perfil del usuario") }
            }
        }
    }
}
