@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.saracervantes.focusmeal.ui.screens.plans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saracervantes.focusmeal.ui.components.FocusMealTextField
import com.saracervantes.focusmeal.ui.theme.PrimaryGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val DIET_LABELS = mapOf(
    "General" to "General",
    "Vegetariana" to "Vegetariana",
    "Keto" to "Keto",
    "Baja en carbohidratos" to "Baja en carbohidratos",
    "Alta en proteínas" to "Alta en proteínas"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    viewModel: PlanViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val expandedDiet = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planes") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading && uiState.plans.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            val error = uiState.error
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Text(
                text = "Crear nuevo plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            FocusMealTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateField("name", it) },
                label = "Nombre del plan",
                enabled = !uiState.isLoading
            )

            FocusMealTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateField("description", it) },
                label = "Descripción",
                enabled = !uiState.isLoading
            )

            FocusMealTextField(
                value = uiState.caloriesPerDay,
                onValueChange = { viewModel.updateField("caloriesPerDay", it) },
                label = "Calorías por día",
                enabled = !uiState.isLoading
            )

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
                    trailingIcon = { androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDiet.value) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedDiet.value,
                    onDismissRequest = { expandedDiet.value = false }
                ) {
                    DIET_LABELS.keys.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateField("dietType", option)
                                expandedDiet.value = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = viewModel::addPlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading && uiState.name.isNotBlank() && uiState.dietType.isNotBlank(),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Crear Plan")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Planes disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.plans) { plan ->
                    PlanCard(plan = plan)
                }
            }

            if (uiState.plans.isEmpty() && !uiState.isLoading) {
                Text(
                    text = "No hay planes disponibles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun PlanCard(plan: com.saracervantes.focusmeal.data.model.Plan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        plan.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        "${plan.caloriesPerDay} kcal/día",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryGreen
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
            Text(
                plan.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                plan.dietType,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
