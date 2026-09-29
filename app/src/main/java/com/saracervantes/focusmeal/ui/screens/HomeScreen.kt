package com.saracervantes.focusmeal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.saracervantes.focusmeal.R
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
import androidx.compose.material3.ButtonDefaults
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
import com.saracervantes.focusmeal.ui.components.TarjetaFocusMeal
import com.saracervantes.focusmeal.ui.screens.home.HomeViewModel
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
                title = { 
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "FocusMeal Logo",
                        modifier = Modifier.height(40.dp)
                    )
                },
                actions = {
                    IconButton(onClick = viewModel::loadDashboard) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
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
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
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
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = todayDate(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        CalorieSummaryCard(uiState)
        Spacer(modifier = Modifier.height(16.dp))
        MacrosRow(uiState)
        Spacer(modifier = Modifier.height(24.dp))
        CaloriesComparisonChart(
            consumed = uiState.totalCalories,
            goal = uiState.goalCalories
        )
        Spacer(modifier = Modifier.height(24.dp))
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
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp).padding(bottom = 16.dp)
        )
        Text(
            text = "No se pudieron cargar tus datos",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Button(
            onClick = onRetry,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
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
        shape = MaterialTheme.shapes.extraLarge, // Rounded generous corners
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Calorías consumidas hoy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${uiState.totalCalories}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "/ ${uiState.goalCalories} kcal",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.tertiary, // Yellow/Mustard pop on green!
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${(fraction * 100).toInt()}% del objetivo diario",
                style = MaterialTheme.typography.labelLarge,
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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        MacroCard(
            label = "Carbohidratos",
            value = uiState.totalCarbs,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
        MacroCard(
            label = "Grasas",
            value = uiState.totalFats,
            color = MaterialTheme.colorScheme.tertiary,
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value.formatMacro(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$label (g)",
                style = MaterialTheme.typography.labelSmall,
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
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondaryContainer

    TarjetaFocusMeal(modifier = modifier) {
        Text(
            text = "Resumen de calorías",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(24.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val barWidth = 48.dp.toPx() 
            val gap = 48.dp.toPx()
            
            val totalWidth = (barWidth * 2) + gap
            val startX = (size.width - totalWidth) / 2f
            
            val consumedHeight = ((consumed / chartMax) * size.height).coerceIn(0f, size.height)
            val goalHeight = ((goal / chartMax) * size.height).coerceIn(0f, size.height)
            val corner = CornerRadius(barWidth / 2f, barWidth / 2f)

            // Consumed Bar
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(startX, size.height - consumedHeight),
                size = Size(barWidth, consumedHeight),
                cornerRadius = corner
            )
            // Goal Bar
            drawRoundRect(
                color = secondaryColor,
                topLeft = Offset(startX + barWidth + gap, size.height - goalHeight),
                size = Size(barWidth, goalHeight),
                cornerRadius = corner
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(primaryColor, "Consumidas:\n$consumed kcal")
            LegendItem(secondaryColor, "Objetivo:\n$goal kcal")
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text, 
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        ActionCard(
            icon = Icons.Filled.Add,
            title = "Agregar comida",
            subtitle = "Registra lo que comiste hoy",
            color = MaterialTheme.colorScheme.primary,
            onClick = onNavigateToAddMeal
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionCard(
            icon = Icons.Filled.DateRange,
            title = "Ver progreso",
            subtitle = "Consulta tu evolución",
            color = MaterialTheme.colorScheme.secondary,
            onClick = onNavigateToProgress
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionCard(
            icon = Icons.Filled.Menu,
            title = "Planes nutricionales",
            subtitle = "Explora tus dietas asignadas",
            color = MaterialTheme.colorScheme.tertiary,
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
    TarjetaFocusMeal(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.15f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
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