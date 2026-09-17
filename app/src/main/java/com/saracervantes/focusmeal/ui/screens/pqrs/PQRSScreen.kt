package com.saracervantes.focusmeal.ui.screens.pqrs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saracervantes.focusmeal.data.model.PQRS
import com.saracervantes.focusmeal.ui.components.FocusMealTextField
import com.saracervantes.focusmeal.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.*

val PQRS_TYPES = listOf(
    PQRS.TYPE_QUESTION,
    PQRS.TYPE_COMPLAINT,
    PQRS.TYPE_RECLAIM,
    PQRS.TYPE_SUGGESTION
)

val STATUS_OPTIONS = listOf(
    PQRS.STATUS_OPEN,
    PQRS.STATUS_IN_PROGRESS,
    PQRS.STATUS_RESOLVED
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PQRSScreen(
    viewModel: PQRSViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val expandedType = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soporte") },
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
            if (uiState.isLoading && uiState.records.isEmpty()) {
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
                text = "Nuevo soporte",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            FocusMealTextField(
                value = uiState.subject,
                onValueChange = { viewModel.updateField("subject", it) },
                label = "Asunto",
                enabled = !uiState.isLoading
            )

            ExposedDropdownMenuBox(
                expanded = expandedType.value,
                onExpandedChange = { expandedType.value = !expandedType.value }
            ) {
                OutlinedTextField(
                    value = uiState.type,
                    onValueChange = { },
                    label = { Text("Tipo") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    trailingIcon = { androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType.value) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedType.value,
                    onDismissRequest = { expandedType.value = false }
                ) {
                    PQRS_TYPES.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateField("type", option)
                                expandedType.value = false
                            }
                        )
                    }
                }
            }

            FocusMealTextField(
                value = uiState.message,
                onValueChange = { viewModel.updateField("message", it) },
                label = "Descripción",
                enabled = !uiState.isLoading
            )

            Button(
                onClick = viewModel::submitPQRS,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading && uiState.message.isNotBlank() && uiState.subject.isNotBlank(),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enviar Solicitud")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mis solicitudes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.records) { record ->
                    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(Date(record.date))
                    PQRSItem(record = record, date = dateStr)
                }
            }

            if (uiState.records.isEmpty() && !uiState.isLoading) {
                Text(
                    text = "Sin solicitudes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun PQRSItem(record: com.saracervantes.focusmeal.data.model.PQRS, date: String) {
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
                Text(
                    record.type,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = PrimaryGreen
                )
                Text(
                    record.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (record.status) {
                        "Open" -> MaterialTheme.colorScheme.error
                        "In Progress" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Text(
                record.subject,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
