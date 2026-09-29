package com.saracervantes.focusmeal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*

@Composable
fun BotonPrimario(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BotonSecundario(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TarjetaFocusMeal(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier.fillMaxWidth()
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), content = content)
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), content = content)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTexto(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            isError = isError,
            trailingIcon = trailingIcon,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun BadgeEspecial(texto: String = "PREMIUM", modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Text(
            text = texto.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusMealTopBar(
    onRefresh: (() -> Unit)? = null,
    navigationIcon: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { 
            Box(modifier = Modifier.fillMaxWidth().padding(end = if (onRefresh != null) 0.dp else 48.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.saracervantes.focusmeal.R.drawable.logo),
                    contentDescription = "FocusMeal Logo",
                    modifier = Modifier.height(48.dp)
                )
            }
        },
        navigationIcon = navigationIcon,
        actions = {
            if (onRefresh != null) {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun FocusMealBottomBar(
    currentRoute: String,
    onNavigateHome: () -> Unit = {},
    onNavigateToAddMeal: () -> Unit = {},
    onNavigateToProgress: () -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        NavigationBarItem(
            selected = currentRoute == "Inicio",
            onClick = onNavigateHome,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Inicio", maxLines = 1) },
            alwaysShowLabel = false,
            colors = colors
        )
        NavigationBarItem(
            selected = currentRoute == "Comidas",
            onClick = onNavigateToAddMeal,
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Comidas", maxLines = 1) },
            alwaysShowLabel = false,
            colors = colors
        )
        NavigationBarItem(
            selected = currentRoute == "Progreso",
            onClick = onNavigateToProgress,
            icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
            label = { Text("Progreso", maxLines = 1) },
            alwaysShowLabel = false,
            colors = colors
        )
        NavigationBarItem(
            selected = currentRoute == "Planes",
            onClick = onNavigateToPlans,
            icon = { Icon(Icons.Filled.Menu, contentDescription = null) },
            label = { Text("Planes", maxLines = 1) },
            alwaysShowLabel = false,
            colors = colors
        )
        NavigationBarItem(
            selected = currentRoute == "Chat",
            onClick = onNavigateToChat,
            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
            label = { Text("Chat", maxLines = 1) },
            alwaysShowLabel = false,
            colors = colors
        )
        NavigationBarItem(
            selected = currentRoute == "Perfil",
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Perfil", maxLines = 1) },
            alwaysShowLabel = false,
            colors = colors
        )
    }
}
