package com.example.vistaraapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vistaraapp.database.ContactDatabase
import com.example.vistaraapp.data.SessionManager
import com.example.vistaraapp.repositories.AuthRepository
import com.example.vistaraapp.screens.RangerProfileScreen


// Data model for an Alert
data class RangerAlert(val id: String, val title: String, val description: String, var isResolved: Boolean = false)

@Composable
fun RangerDashboard(onLogoutSuccess: () -> Unit) {
    val navController = rememberNavController()
    // Managing the list of alerts in the UI state
    var alertList by remember { mutableStateOf(listOf(
        RangerAlert("1", "Emergency SOS", "Visitor location: 1.2921, 36.8219"),
        RangerAlert("2", "Vehicle Breakdown", "KBC 123A reported at Gate B")
    )) }

    val context = LocalContext.current
    val contactDao = remember { ContactDatabase.getDatabase(context).dao }
    val sessionManager = remember { SessionManager(context) }
    val rangerViewModel: RangerProfileViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                RangerProfileViewModel(
                    authRepository = AuthRepository(contactDao),
                    sessionManager = sessionManager
                )
            }
        }
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                listOf("Dashboard", "Scanner", "Alerts", "Profile").forEach { route ->
                    NavigationBarItem(
                        icon = { Icon(when(route) {
                            "Dashboard" -> Icons.Default.Home
                            "Scanner" -> Icons.Default.QrCodeScanner
                            "Alerts" -> Icons.Default.Notifications
                            else -> Icons.Default.Person
                        }, null) },
                        label = { Text(route) },
                        selected = currentRoute == route,
                        onClick = { navController.navigate(route) { launchSingleTop = true } }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "Dashboard", Modifier.padding(innerPadding)) {
            composable("Dashboard") { RangerHomeContent() }
            composable("Scanner") { Text("Scanner Logic Here") }
            composable("Alerts") {
                RangerAlertsContent(alerts = alertList) { idToResolve ->
                    // Update state to mark alert as resolved
                    alertList = alertList.map {
                        if (it.id == idToResolve) it.copy(isResolved = true) else it
                    }
                }
            }
            composable("Profile") {
                RangerProfileScreen(
                    viewModel = rangerViewModel,
                    onLogoutSuccess = onLogoutSuccess
                )
            }
        }
    }
}

@Composable
fun RangerHomeContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ranger Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Welcome, Ranger", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Monitor alerts, scan visitor tickets, and manage park activities from here.")
            }
        }
    }
}

@Composable
fun RangerAlertsContent(alerts: List<RangerAlert>, onResolve: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(alerts) { alert ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (alert.isResolved) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(alert.title, fontWeight = FontWeight.Bold)
                    Text(alert.description)
                    if (!alert.isResolved) {
                        Button(onClick = { onResolve(alert.id) }) { Text("Mark Resolved") }
                    } else {
                        Text("Resolved", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}