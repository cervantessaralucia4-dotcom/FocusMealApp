package com.saracervantes.focusmeal.ui.screens.subscription

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saracervantes.focusmeal.data.util.Resource
import com.saracervantes.focusmeal.ui.components.BadgeEspecial
import com.saracervantes.focusmeal.ui.components.BotonPrimario
import com.saracervantes.focusmeal.ui.components.BotonSecundario
import com.saracervantes.focusmeal.ui.components.TarjetaFocusMeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onBack: () -> Unit
) {
    val purchaseState by viewModel.purchaseState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(purchaseState) {
        when (purchaseState) {
            is Resource.Success -> {
                Toast.makeText(context, "¡Felicidades! Ahora eres Premium 🎉", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onBack()
            }
            is Resource.Error -> {
                Toast.makeText(context, "Error: ${(purchaseState as Resource.Error).message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planes de Suscripción", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (purchaseState is Resource.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = "Mejora tu experiencia",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Elige el plan que mejor se adapte a tus objetivos y alcanza tus metas más rápido.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    item {
                        PlanCard(
                            title = "Plan Gratis",
                            price = "$0.00 / mes",
                            features = listOf("Creación de planes básicos", "Registro de comidas manual", "Perfil de usuario"),
                            isPremium = false,
                            onSelect = { /* Ya es gratis por defecto */ }
                        )
                    }

                    item {
                        PlanCard(
                            title = "Plan Premium PRO",
                            price = "$4.99 / mes",
                            features = listOf("Todo lo del Plan Gratis", "Chat en Vivo con Nutricionistas", "Generación de Planes con IA", "Sin anuncios"),
                            isPremium = true,
                            isRecommended = true,
                            onSelect = { viewModel.purchasePremium() }
                        )
                    }
                    
                    item {
                        PlanCard(
                            title = "Plan Premium ANUAL",
                            price = "$49.99 / año",
                            features = listOf("Todo lo de Premium PRO", "Ahorra un 15%", "Soporte prioritario"),
                            isPremium = true,
                            onSelect = { viewModel.purchasePremium() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    features: List<String>,
    isPremium: Boolean,
    isRecommended: Boolean = false,
    onSelect: () -> Unit
) {
    TarjetaFocusMeal(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isRecommended) {
                BadgeEspecial("MÁS POPULAR")
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isPremium) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = price,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                features.forEach { feature ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = if (isPremium) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (isPremium) {
                BotonPrimario(text = "Seleccionar Plan", onClick = onSelect)
            } else {
                BotonSecundario(text = "Plan Actual", onClick = onSelect)
            }
        }
    }
}
