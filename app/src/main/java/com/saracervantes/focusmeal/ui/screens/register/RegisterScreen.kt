@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.saracervantes.focusmeal.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RestaurantMenu
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.RestaurantMenu,
                    contentDescription = "Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 12.dp)
                )
                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Empieza tu camino a comer mejor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

        FocusMealTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nombre Completo"
        )

        FocusMealTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico"
        )

        FocusMealTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
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
                    .menuAnchor()
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
                    .menuAnchor()
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
                text = state.message ?: "Error al registrarse",
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
                Text("Registrarse")
            }
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes una cuenta? Inicia sesión")
        }
            }
        }
    }
}
