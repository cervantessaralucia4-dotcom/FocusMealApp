package com.saracervantes.focusmeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saracervantes.focusmeal.di.AppModule
import com.saracervantes.focusmeal.ui.screens.HomeScreen
import com.saracervantes.focusmeal.ui.screens.LoginScreen
import com.saracervantes.focusmeal.ui.screens.SectionScreen
import com.saracervantes.focusmeal.ui.screens.addmeal.AddMealScreen
import com.saracervantes.focusmeal.ui.screens.addmeal.AddMealViewModel
import com.saracervantes.focusmeal.ui.screens.home.HomeViewModel
import com.saracervantes.focusmeal.ui.screens.login.LoginViewModel
import com.saracervantes.focusmeal.ui.screens.chat.ChatScreen
import com.saracervantes.focusmeal.ui.screens.chat.ChatViewModel
import com.saracervantes.focusmeal.ui.screens.pqrs.PQRSViewModel
import com.saracervantes.focusmeal.ui.screens.pqrs.PQRSScreen
import com.saracervantes.focusmeal.ui.screens.plans.PlansScreen
import com.saracervantes.focusmeal.ui.screens.plans.PlanViewModel
import com.saracervantes.focusmeal.ui.screens.progress.ProgressScreen
import com.saracervantes.focusmeal.ui.screens.progress.ProgressViewModel
import com.saracervantes.focusmeal.ui.screens.profile.ProfileScreen
import com.saracervantes.focusmeal.ui.screens.profile.ProfileViewModel
import com.saracervantes.focusmeal.ui.screens.register.RegisterScreen
import com.saracervantes.focusmeal.ui.screens.register.RegisterViewModel
import com.saracervantes.focusmeal.ui.theme.FocusMealTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusMealTheme {
                FocusMealApp()
            }
        }
    }
}

@Composable
fun FocusMealApp() {
    val navController = rememberNavController()
    val authRepository = AppModule.authRepository
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                val viewModel: LoginViewModel = viewModel {
                    LoginViewModel(authRepository)
                }
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                val viewModel: RegisterViewModel = viewModel {
                    RegisterViewModel(authRepository)
                }
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable("home") {
                val viewModel: HomeViewModel = viewModel {
                    HomeViewModel(
                        mealRepository = AppModule.mealRepository,
                        planRepository = AppModule.planRepository,
                        userRepository = AppModule.userRepository,
                        authRepository = AppModule.authRepository
                    )
                }
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAddMeal = { navController.navigate("addMeal") },
                    onNavigateToProgress = { navController.navigate("progress") },
                    onNavigateToPlans = { navController.navigate("plans") },
                    onNavigateToChat = { navController.navigate("chat") },
                    onNavigateToProfile = { navController.navigate("profile") }
                )
            }
            composable("addMeal") {
                val addMealViewModel: AddMealViewModel = viewModel {
                    AddMealViewModel(
                        mealRepository = AppModule.mealRepository,
                        authRepository = AppModule.authRepository
                    )
                }
                AddMealScreen(
                    viewModel = addMealViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToMeals = { navController.popBackStack() }
                )
            }
            composable("progress") {
                val progressViewModel: ProgressViewModel = viewModel {
                    ProgressViewModel(
                        progressRepository = AppModule.progressRepository,
                        authRepository = AppModule.authRepository
                    )
                }
                ProgressScreen(
                    viewModel = progressViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("plans") {
                val planViewModel: PlanViewModel = viewModel {
                    PlanViewModel(
                        planRepository = AppModule.planRepository,
                        authRepository = AppModule.authRepository
                    )
                }
                PlansScreen(
                    viewModel = planViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("profile") {
                val profileViewModel: ProfileViewModel = viewModel {
                    ProfileViewModel(
                        userRepository = AppModule.userRepository,
                        authRepository = AppModule.authRepository
                    )
                }
                ProfileScreen(
                    viewModel = profileViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("chat") {
                val chatViewModel: ChatViewModel = viewModel {
                    ChatViewModel(
                        chatRepository = AppModule.chatRepository,
                        authRepository = AppModule.authRepository
                    )
                }
                ChatScreen(
                    viewModel = chatViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("pqrs") {
                val pqrsViewModel = PQRSViewModel(
                    pqrsRepository = AppModule.pqrsRepository,
                    authRepository = AppModule.authRepository
                )
                PQRSScreen(
                    viewModel = pqrsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
