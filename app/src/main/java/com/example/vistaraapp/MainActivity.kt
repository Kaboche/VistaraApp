package com.example.vistaraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vistaraapp.ui.theme.VistaraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            VistaraTheme {
                VistaraApp()
            }
        }
    }
}

@Composable
fun VistaraApp() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"
                val showBottomBar = currentRoute in listOf("home", "wildlife", "bookings", "profile")

                if (showBottomBar) {
                    ModernBottomBar(
                        currentRoute = currentRoute,
                        onItemSelected = { route ->
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            // ========== AUTH SCREENS ==========
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        isLoggedIn = true
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                RegisterScreen(navController = navController)
            }

            // ✅ FIXED: Forgot Password Route
            composable("forgot_password") {
                val authViewModel: AuthViewModel = viewModel()
                ForgotPasswordScreen(
                    authViewModel = authViewModel,
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            // ========== MAIN BOTTOM NAVIGATION SCREENS ==========
            composable("home") {
                val weatherViewModel: WeatherViewModel = viewModel()
                HomeScreen(navController = navController, weatherViewModel = weatherViewModel)
            }

            composable("wildlife") {
                WildlifeScreen(navController = navController)
            }

            composable("bookings") {
                BookingsScreen(navController = navController)
            }

            composable("profile") {
                ProfileScreen(navController = navController)
            }

            // ========== BOOKING FLOW SCREENS ==========
            composable("booking/{parkId}") { backStackEntry ->
                val parkId = backStackEntry.arguments?.getString("parkId")?.toIntOrNull() ?: 0
                BookingScreen(navController = navController, parkId = parkId)
            }

            // ✅ CHECKIN SCREEN
            composable("checkin") {
                CheckInScreen(navController = navController)
            }

            // ✅ MAP TRACKING SCREEN
            composable("map_tracking") {
                MapTrackingScreen(navController = navController)
            }

            // ========== ANIMAL DETAIL SCREEN ==========
            composable("animal/{animalId}") { backStackEntry ->
                val animalId = backStackEntry.arguments?.getString("animalId")?.toIntOrNull() ?: 1
                val animal = uniqueAnimals.find { it.id == animalId } ?: uniqueAnimals[0]
                AnimalDetailScreen(navController = navController, animal = animal)
            }

            composable("notifications") {
                NotificationScreen(navController = navController)
            }
        }
    }
}