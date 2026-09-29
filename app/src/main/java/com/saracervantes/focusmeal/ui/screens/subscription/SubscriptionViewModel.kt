package com.saracervantes.focusmeal.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.UserRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _purchaseState = MutableStateFlow<Resource<Boolean>?>(null)
    val purchaseState: StateFlow<Resource<Boolean>?> = _purchaseState

    fun purchasePremium() {
        val userId = authRepository.currentUser?.uid
        if (userId == null) {
            _purchaseState.value = Resource.Error("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            _purchaseState.value = Resource.Loading()
            
            // Simular un retraso de pasarela de pago (Google Play Billing o Stripe Mock)
            kotlinx.coroutines.delay(2000)
            
            when (val userResult = userRepository.getUser(userId)) {
                is Resource.Success -> {
                    val user = userResult.data
                    if (user != null) {
                        val updatedUser = user.copy(isPremium = true)
                        val updateResult = userRepository.updateUser(updatedUser)
                        _purchaseState.value = updateResult
                    } else {
                        _purchaseState.value = Resource.Error("Perfil de usuario no encontrado")
                    }
                }
                is Resource.Error -> {
                    _purchaseState.value = Resource.Error(userResult.message ?: "Error desconocido")
                }
                else -> {}
            }
        }
    }
    
    fun resetState() {
        _purchaseState.value = null
    }
}
