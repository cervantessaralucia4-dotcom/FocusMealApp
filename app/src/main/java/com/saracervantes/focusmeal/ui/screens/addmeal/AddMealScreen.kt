@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.saracervantes.focusmeal.ui.screens.addmeal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saracervantes.focusmeal.ui.components.FocusMealTextField

val MEAL_CATEGORIES = listOf(
    "Desayuno", "Almuerzo", "Cena", "Merienda", "Snack", "Postre"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    viewModel: AddMealViewModel,
    onBack: () -> Unit,
    onNavigateToMeals: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val expandedCategory = remember { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onNavigateToMeals()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Comida") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            FocusMealTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateField("name", it) },
                label = "Nombre del plato",
                enabled = !uiState.isLoading
            )

            ExposedDropdownMenuBox(
                    expanded = expandedCategory.value,
                    onExpandedChange = { expandedCategory.value = !expandedCategory.value }
                ) {
                    OutlinedTextField(
                        value = uiState.category,
                        onValueChange = { },
                        label = { Text("Categoría") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory.value) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory.value,
                        onDismissRequest = { expandedCategory.value = false }
                    ) {
                        MEAL_CATEGORIES.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.updateField("category", option)
                                    expandedCategory.value = false
                                }
                            )
                        }
                    }
                }

            FocusMealTextField(
                value = uiState.category,
                onValueChange = { viewModel.updateField("category", it) },
                label = "Categoría",
                enabled = !uiState.isLoading
            )

            FocusMealTextField(
                value = uiState.calories,
                onValueChange = { viewModel.updateField("calories", it) },
                label = "Calorías (kcal)",
                enabled = !uiState.isLoading
            )

            FocusMealTextField(
                value = uiState.proteins,
                onValueChange = { viewModel.updateField("proteins", it) },
                label = "Proteínas (g)",
                enabled = !uiState.isLoading
            )

            FocusMealTextField(
                value = uiState.carbs,
                onValueChange = { viewModel.updateField("carbs", it) },
                label = "Carbohidratos (g)",
                enabled = !uiState.isLoading
            )

            FocusMealTextField(
                value = uiState.fats,
                onValueChange = { viewModel.updateField("fats", it) },
                label = "Grasas (g)",
                enabled = !uiState.isLoading
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            val error = uiState.error
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::addMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading && uiState.name.isNotBlank(),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar Comida")
                }
            }
        }
    }
}
