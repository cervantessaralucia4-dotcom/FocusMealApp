@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.saracervantes.focusmeal.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saracervantes.focusmeal.data.model.User
import com.saracervantes.focusmeal.ui.components.FocusMealTextField

val GOAL_OPTIONS = listOf(
    User.GOAL_LOSE_WEIGHT,
    User.GOAL_MAINTAIN_WEIGHT,
    User.GOAL_GAIN_MASS
)

val DIET_OPTIONS = listOf(
    User.DIET_GENERAL,
    User.DIET_VEGETARIAN,
    User.DIET_KETO,
    User.DIET_LOW_CARB,
    User.DIET_HIGH_PROTEIN
)

val GENDER_OPTIONS = listOf("Masculino", "Femenino", "Otro", "Prefiero no decirlo")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val expandedGoal = remember { mutableStateOf(false) }
    val expandedDiet = remember { mutableStateOf(false) }
    val expandedGender = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!uiState.isLoading && uiState.user != null && !uiState.isEditing) {
                        IconButton(onClick = viewModel::toggleEdit) {
                            Text("Editar", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (uiState.isEditing) {
                        IconButton(onClick = viewModel::saveProfile) {
                            Icon(Icons.Filled.Save, contentDescription = "Guardar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                ProfileForm(
                    uiState = uiState,
                    onFieldChange = viewModel::updateField,
                    expandedGoal = expandedGoal,
                    expandedDiet = expandedDiet,
                    expandedGender = expandedGender,
                    isEditing = uiState.isEditing
                )
            }
        }
    }
}

@Composable
private fun ProfileForm(
    uiState: ProfileViewModel.ProfileUiState,
    onFieldChange: (String, String) -> Unit,
    expandedGoal: MutableState<Boolean>,
    expandedDiet: MutableState<Boolean>,
    expandedGender: MutableState<Boolean>,
    isEditing: Boolean
) {
    FocusMealTextField(
        value = uiState.name,
        onValueChange = { onFieldChange("name", it) },
        label = "Nombre",
        enabled = isEditing
    )

    FocusMealTextField(
        value = uiState.email,
        onValueChange = { },
        label = "Email",
        enabled = false
    )

    FocusMealTextField(
        value = uiState.age,
        onValueChange = { onFieldChange("age", it) },
        label = "Edad",
        enabled = isEditing
    )

    if (isEditing) {
        ExposedDropdownMenuBox(
            expanded = expandedGender.value,
            onExpandedChange = { expandedGender.value = !expandedGender.value }
        ) {
            OutlinedTextField(
                value = uiState.gender,
                onValueChange = { },
                label = { Text("Género") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
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
                GENDER_OPTIONS.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onFieldChange("gender", option)
                            expandedGender.value = false
                        }
                    )
                }
            }
        }
    } else {
        FocusMealTextField(
            value = uiState.gender,
            onValueChange = { },
            label = "Género",
            enabled = false
        )
    }

    FocusMealTextField(
        value = uiState.weight,
        onValueChange = { onFieldChange("weight", it) },
        label = "Peso (kg)",
        enabled = isEditing
    )

    FocusMealTextField(
        value = uiState.height,
        onValueChange = { onFieldChange("height", it) },
        label = "Altura (cm)",
        enabled = isEditing
    )

    if (isEditing) {
        ExposedDropdownMenuBox(
            expanded = expandedGoal.value,
            onExpandedChange = { expandedGoal.value = !expandedGoal.value }
        ) {
            OutlinedTextField(
                value = uiState.goal,
                onValueChange = { },
                label = { Text("Objetivo") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGoal.value) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expandedGoal.value,
                onDismissRequest = { expandedGoal.value = false }
            ) {
                GOAL_OPTIONS.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onFieldChange("goal", option)
                            expandedGoal.value = false
                        }
                    )
                }
            }
        }
    } else {
        FocusMealTextField(
            value = uiState.goal,
            onValueChange = { },
            label = "Objetivo",
            enabled = false
        )
    }

    if (isEditing) {
        ExposedDropdownMenuBox(
            expanded = expandedDiet.value,
            onExpandedChange = { expandedDiet.value = !expandedDiet.value }
        ) {
            OutlinedTextField(
                value = uiState.dietType,
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
                DIET_OPTIONS.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onFieldChange("dietType", option)
                            expandedDiet.value = false
                        }
                    )
                }
            }
        }
    } else {
        FocusMealTextField(
            value = uiState.dietType,
            onValueChange = { },
            label = "Tipo de Dieta",
            enabled = false
        )
    }

    val error = uiState.error
    if (isEditing && error != null) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
