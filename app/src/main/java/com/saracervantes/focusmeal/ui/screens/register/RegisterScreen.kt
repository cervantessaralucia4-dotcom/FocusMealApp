@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.saracervantes.focusmeal.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saracervantes.focusmeal.data.model.User
import com.saracervantes.focusmeal.data.util.Resource
import com.saracervantes.focusmeal.ui.components.FocusMealTextField

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dietType by remember { mutableStateOf("") }
    val expandedGender = remember { mutableStateOf(false) }
    val expandedDiet = remember { mutableStateOf(false) }
    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        if (registerState is Resource.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setExtraFields(
            age.toIntOrNull() ?: 0,
            gender,
            dietType
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Start your FocusMeal journey today",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        FocusMealTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name"
        )

        FocusMealTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email"
        )

        FocusMealTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )

        FocusMealTextField(
            value = age,
            onValueChange = { age = it },
            label = "Edad"
        )

        ExposedDropdownMenuBox(
            expanded = expandedGender.value,
            onExpandedChange = { expandedGender.value = !expandedGender.value }
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = { },
                label = { Text("Género") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender.value) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expandedGender.value,
                onDismissRequest = { expandedGender.value = false }
            ) {
                User.GENDER_OPTIONS.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            gender = option
                            expandedGender.value = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = expandedDiet.value,
            onExpandedChange = { expandedDiet.value = !expandedDiet.value }
        ) {
            OutlinedTextField(
                value = dietType,
                onValueChange = { },
                label = { Text("Tipo de Dieta") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDiet.value) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expandedDiet.value,
                onDismissRequest = { expandedDiet.value = false }
            ) {
                User.DIET_OPTIONS.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            dietType = option
                            expandedDiet.value = false
                        }
                    )
                }
            }
        }

        val state = registerState
        if (state is Resource.Error) {
            Text(
                text = state.message ?: "Registration failed",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.register(email, password, name) },
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is Resource.Loading,
            shape = MaterialTheme.shapes.medium
        ) {
            if (registerState is Resource.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Register")
            }
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Sign In")
        }
    }
}
