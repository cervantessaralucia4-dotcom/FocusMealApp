package com.saracervantes.focusmeal.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.UserRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<String>?>(null)
    val loginState: StateFlow<Resource<String>?> = _loginState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = Resource.Error("Fields cannot be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val signInResult = authRepository.signIn(email, pass)
            
            if (signInResult is Resource.Success) {
                val userId = authRepository.currentUser?.uid
                if (userId != null) {
                    val userResult = userRepository.getUser(userId)
                    if (userResult is Resource.Success && userResult.data != null) {
                        val role = userResult.data.role
                        val route = when (role) {
                            "nutricionista" -> "nutritionist_dashboard"
                            "admin" -> "admin_dashboard"
                            else -> "home"
                        }
                        _loginState.value = Resource.Success(route)
                    } else {
                        _loginState.value = Resource.Error("No se pudo obtener el perfil del usuario")
                    }
                } else {
                    _loginState.value = Resource.Error("Error de sesión")
                }
            } else if (signInResult is Resource.Error) {
                _loginState.value = Resource.Error(signInResult.message ?: "Error desconocido")
            }
        }
    }
    
    fun resetState() {
        _loginState.value = null
    }
}
