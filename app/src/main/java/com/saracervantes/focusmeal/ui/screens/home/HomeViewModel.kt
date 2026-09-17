package com.saracervantes.focusmeal.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.model.Meal
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.MealRepository
import com.saracervantes.focusmeal.data.repository.PlanRepository
import com.saracervantes.focusmeal.data.repository.UserRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val mealRepository: MealRepository,
    private val planRepository: PlanRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class HomeUiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val userName: String = "",
        val todayMeals: List<Meal> = emptyList(),
        val totalCalories: Int = 0,
        val totalProteins: Float = 0f,
        val totalCarbs: Float = 0f,
        val totalFats: Float = 0f,
        val goalCalories: Int = DEFAULT_GOAL_CALORIES
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = authRepository.currentUser?.uid
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "No hay una sesión activa") }
                return@launch
            }

            var todayMeals = emptyList<Meal>()
            var error: String? = null

            when (val result = mealRepository.getMeals(userId)) {
                is Resource.Success -> {
                    val (start, end) = todayRangeMs()
                    todayMeals = result.data.orEmpty().filter { it.date in start until end }
                }
                is Resource.Error -> error = result.message
                is Resource.Loading -> Unit
            }

            val goal = loadGoalCalories(userId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = error,
                    todayMeals = todayMeals,
                    totalCalories = todayMeals.sumOf { meal -> meal.calories },
                    totalProteins = todayMeals.fold(0f) { acc, meal -> acc + meal.proteins },
                    totalCarbs = todayMeals.fold(0f) { acc, meal -> acc + meal.carbs },
                    totalFats = todayMeals.fold(0f) { acc, meal -> acc + meal.fats },
                    goalCalories = goal
                )
            }
        }
    }

    private suspend fun loadGoalCalories(userId: String): Int {
        val user = when (val result = userRepository.getUser(userId)) {
            is Resource.Success -> result.data
            else -> null
        } ?: return DEFAULT_GOAL_CALORIES

        val dietType = user.dietType
        if (dietType.isBlank()) return DEFAULT_GOAL_CALORIES

        return when (val result = planRepository.getPlans()) {
            is Resource.Success ->
                result.data?.firstOrNull { it.dietType == dietType }?.caloriesPerDay
                    ?: DEFAULT_GOAL_CALORIES
            else -> DEFAULT_GOAL_CALORIES
        }
    }

    private fun todayRangeMs(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return start to calendar.timeInMillis
    }

    companion object {
        const val DEFAULT_GOAL_CALORIES = 2000
    }
}