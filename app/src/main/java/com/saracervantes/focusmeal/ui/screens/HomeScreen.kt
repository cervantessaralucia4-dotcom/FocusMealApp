package com.saracervantes.focusmeal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saracervantes.focusmeal.ui.screens.home.HomeViewModel
import com.saracervantes.focusmeal.ui.theme.PrimaryGreen
import com.saracervantes.focusmeal.ui.theme.SecondaryGreen
import com.saracervantes.focusmeal.ui.theme.TertiaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddMeal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToPlans: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("FocusMeal") },
                actions = {
                    IconButton(onClick = viewModel::loadDashboard) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToAddMeal,
                    icon = { Icon(Icons.Filled.List, contentDescription = null) },
                    label = { Text("Comidas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProgress,
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Progreso") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToPlans,
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text("Planes") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToChat,
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    label = { Text("Chat") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.error != null -> ErrorContent(
                    message = uiState.error.orEmpty(),
                    onRetry = viewModel::loadDashboard,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> DashboardContent(
                    uiState = uiState,
                    onNavigateToAddMeal = onNavigateToAddMeal,
                    onNavigateToProgress = onNavigateToProgress,
                    onNavigateToPlans = onNavigateToPlans
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: HomeViewModel.HomeUiState,
    onNavigateToAddMeal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToPlans: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Hola, ${uiState.userName.ifBlank { "usuario" }}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = todayDate(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        CalorieSummaryCard(uiState)
        Spacer(modifier = Modifier.height(12.dp))
        MacrosRow(uiState)
        Spacer(modifier = Modifier.height(16.dp))
        CaloriesComparisonChart(
            consumed = uiState.totalCalories,
            goal = uiState.goalCalories
        )
        Spacer(modifier = Modifier.height(16.dp))
        QuickActionsSection(
            onNavigateToAddMeal = onNavigateToAddMeal,
            onNavigateToProgress = onNavigateToProgress,
            onNavigateToPlans = onNavigateToPlans
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No se pudieron cargar tus datos",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun CalorieSummaryCard(uiState: HomeViewModel.HomeUiState) {
    val fraction = if (uiState.goalCalories > 0) {
        (uiState.totalCalories.toFloat() / uiState.goalCalories).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Calorías consumidas hoy",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${uiState.totalCalories}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "/ ${uiState.goalCalories} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(fraction * 100).toInt()}% del objetivo diario",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun MacrosRow(uiState: HomeViewModel.HomeUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MacroCard(
            label = "Proteínas",
            value = uiState.totalProteins,
            color = PrimaryGreen,
            modifier = Modifier.weight(1f)
        )
        MacroCard(
            label = "Carbohidratos",
            value = uiState.totalCarbs,
            color = SecondaryGreen,
            modifier = Modifier.weight(1f)
        )
        MacroCard(
            label = "Grasas",
            value = uiState.totalFats,
            color = TertiaryGreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MacroCard(
    label: String,
    value: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value.formatMacro(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$label (g)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CaloriesComparisonChart(
    consumed: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val chartMax = max(consumed.toFloat(), goal.toFloat()).coerceAtLeast(1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Calorías: consumidas vs objetivo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val gap = size.width * 0.15f
                val barWidth = (size.width - gap * 3f) / 2f
                val consumedHeight = ((consumed / chartMax) * size.height).coerceIn(0f, size.height)
                val goalHeight = ((goal / chartMax) * size.height).coerceIn(0f, size.height)
                val corner = CornerRadius(barWidth / 3f, barWidth / 3f)

                drawRoundRect(
                    color = PrimaryGreen,
                    topLeft = Offset(gap, size.height - consumedHeight),
                    size = Size(barWidth, consumedHeight),
                    cornerRadius = corner
                )
                drawRoundRect(
                    color = Color(0xFFBDBDBD),
                    topLeft = Offset(gap * 2f + barWidth, size.height - goalHeight),
                    size = Size(barWidth, goalHeight),
                    cornerRadius = corner
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem(PrimaryGreen, "Consumidas: $consumed kcal")
                LegendItem(Color(0xFFBDBDBD), "Objetivo: $goal kcal")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToAddMeal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToPlans: () -> Unit
) {
    Column {
        Text(
            text = "Accesos rápidos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionCard(
            icon = Icons.Filled.Add,
            title = "Agregar comida",
            subtitle = "Registra lo que comiste hoy",
            color = PrimaryGreen,
            onClick = onNavigateToAddMeal
        )
        Spacer(modifier = Modifier.height(8.dp))
        ActionCard(
            icon = Icons.Filled.DateRange,
            title = "Ver progreso",
            subtitle = "Consulta tu evolución",
            color = SecondaryGreen,
            onClick = onNavigateToProgress
        )
        Spacer(modifier = Modifier.height(8.dp))
        ActionCard(
            icon = Icons.Filled.Menu,
            title = "Planes",
            subtitle = "Explora tus planes nutricionales",
            color = TertiaryGreen,
            onClick = onNavigateToPlans
        )
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun Float.formatMacro(): String =
    if (this % 1f == 0f) this.toInt().toString() else String.format(Locale.ROOT, "%.1f", this)

private fun todayDate(): String =
    SimpleDateFormat("EEEE, d 'de' MMMM yyyy", Locale("es")).format(Date())