package com.example.vistaraapp.screens.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vistaraapp.api.RetrofitClient
import com.example.vistaraapp.ProfileNetworkRequest
import com.example.vistaraapp.data.SessionManager
import com.example.vistaraapp.database.*
import com.example.vistaraapp.entities_dataclass.uniqueAnimals
import com.example.vistaraapp.repositories.*
import com.example.vistaraapp.screens.*

import com.example.vistaraapp.viewmodels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppNavigation(
    navController: NavHostController,
    contactViewModel: ContactViewModel,
    contactState: ContactState,
    contactDao: ContactDao,
    sessionToken: String,
    onTokenUpdated: (String) -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val tokenState = rememberUpdatedState(sessionToken)
    val currentContactState = rememberUpdatedState(contactState)
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {

        //  LOGIN
        composable("login") {

            val loginViewModel: LoginViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val repo = AuthRepository(contactDao)

                        LoginViewModel(
                            authRepository = repo,
                            sessionManager = sessionManager
                        )
                    }
                }
            )

            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToDashboard = { token ->
                    onTokenUpdated(token)
                    onLoginSuccess()

                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToResetPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }

        //  REGISTER
        composable("register") {

            val authViewModel: AuthViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        AuthRepository(contactDao).let { AuthViewModel(it) }
                    }
                }
            )

            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        //  FORGOT PASSWORD
        composable("forgot_password") {

            val authViewModel: AuthViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        AuthRepository(contactDao).let { AuthViewModel(it) }
                    }
                }
            )

            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onBackToLogin = { navController.popBackStack() }
            )
        }

        //  HOME
        composable("home") {
            val weatherViewModel: WeatherViewModel = viewModel()
            HomeScreen(navController, weatherViewModel)
        }

        //  WILDLIFE
        composable("wildlife") {
            WildlifeScreen(navController)
        }

        //  BOOKINGS
        composable("bookings") {

            val bookingViewModel: BookingViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        BookingViewModel(
                            BookingRepository(RetrofitClient.bookingInstance)
                        )
                    }
                }
            )

            BookingsScreen(
                navController = navController,
                viewModel = bookingViewModel,
                authToken = tokenState.value
            )
        }

        //  PROFILE
        composable("profile") {

            ProfileScreen(
                navController = navController,
                state = currentContactState.value,
                onEvent = contactViewModel::onEvent,
                authToken = tokenState.value
            )
        }

        //  EDIT PROFILE
        composable("edit_profile") {

            EditProfileScreen(
                navController = navController,
                state = currentContactState.value,
                onEvent = contactViewModel::onEvent,
                onSaveProfileApi = { fullName, phone, email, emergencyPhone ->

                    coroutineScope.launch(Dispatchers.IO) {
                        try {

                            val requestPayload = ProfileNetworkRequest(
                                fullName = fullName,
                                phoneNumber = phone.replace(Regex("[^0-9]"), ""),
                                emergencyContactName = "Emergency Contact",
                                emergencyContactPhone = emergencyPhone.replace(Regex("[^0-9]"), ""),
                                nationalId = currentContactState.value.idNumber
                            )

                            val response = RetrofitClient.profileInstance.saveProfileDetails(
                                bearerToken = "Bearer ${tokenState.value}",
                                profileData = requestPayload
                            )

                            if (response.isSuccessful) {
                                withContext(Dispatchers.Main) {
                                    navController.popBackStack()
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
        }

        //  ANIMAL DETAILS
        composable("animal/{animalId}") { backStackEntry ->

            val animalId = backStackEntry.arguments
                ?.getString("animalId")
                ?.toIntOrNull() ?: 1

            val animal = uniqueAnimals.find { it.id == animalId } ?: uniqueAnimals[0]

            AnimalDetailScreen(
                animal = animal,
                navController = navController
            )
        }

        // ───────── BOOKING FORM ─────────
        composable("booking/{parkId}") { backStackEntry ->
            val parkId = backStackEntry.arguments
                ?.getString("parkId")
                ?.toIntOrNull() ?: 1
            BookingScreen(
                navController = navController,
                parkId = parkId,
                state = currentContactState.value,
                onEvent = contactViewModel::onEvent
            )
        }

        // ───────── RESET PASSWORD ─────────
        composable("reset_password") {
            ResetPasswordScreen(navController = navController)
        }

        // ───────── EXTRA SCREENS ─────────
        composable("check_in") { CheckInScreen(navController) }
        composable("map_tracking") { MapTrackingScreen(navController) }
        composable("notifications") { NotificationScreen(navController) }
    }
}