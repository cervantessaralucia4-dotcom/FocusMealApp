package com.saracervantes.focusmeal.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<Boolean>?>(null)
    val registerState: StateFlow<Resource<Boolean>?> = _registerState

    private var age: Int = 0
    private var gender: String = ""
    private var dietType: String = ""

    fun setExtraFields(age: Int, gender: String, dietType: String) {
        this.age = age
        this.gender = gender
        this.dietType = dietType
    }

    fun register(email: String, pass: String, name: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _registerState.value = Resource.Error("Fields cannot be empty")
            return
        }

        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            _registerState.value = repository.signUp(email, pass, name, age, gender, dietType)
        }
    }
    
    fun resetState() {
        _registerState.value = null
        age = 0
        gender = ""
        dietType = ""
    }
}
