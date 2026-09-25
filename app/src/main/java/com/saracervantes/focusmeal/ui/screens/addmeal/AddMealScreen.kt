@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.saracervantes.focusmeal.ui.screens.addmeal

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.provider.MediaStore
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
    
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                // Ensure bitmap is in a compatible format for Gemini
                val softwareBitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true)
                viewModel.analyzeImage(softwareBitmap)
            }
        }
    )

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Deja que la IA calcule tus calorías!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Subir foto de mi plato")
                    }
                }
            }
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
                            .menuAnchor()
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
