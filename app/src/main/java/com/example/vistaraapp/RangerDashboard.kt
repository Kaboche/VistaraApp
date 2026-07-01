package com.example.vistaraapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vistaraapp.database.ContactDatabase
import com.example.vistaraapp.data.SessionManager
import com.example.vistaraapp.repositories.AuthRepository
import com.example.vistaraapp.screens.RangerProfileScreen
import androidx.compose.ui.draw.clip
import com.example.vistaraapp.screens.RangerScannerContent
import com.example.vistaraapp.screens.navigation.RangerBottomBar

// Updated Data model for an SOS Alert
data class RangerSosAlert(
    val id: String,
    val visitorName: String,
    val alertType: String,
    val status: String, // "Unresolved", "Assigned", "Resolved"
    val assignedRanger: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangerDashboard(
    onLogoutSuccess: () -> Unit,
    onResetPasswordClick: (() -> Unit)? = null
) {
    val navController = rememberNavController()
    
    // Shared state of SOS Alerts
    var sosAlerts by remember { mutableStateOf(listOf(
        RangerSosAlert("1", "John Doe", "Medical Emergency", "Unresolved"),
        RangerSosAlert("2", "Alice Smith", "Wildlife Encounter", "Assigned", "Officer Ranger"),
        RangerSosAlert("3", "David Kim", "Vehicle Breakdown", "Resolved", "Officer Ranger"),
        RangerSosAlert("4", "Sarah Connor", "Lost", "Unresolved"),
        RangerSosAlert("5", "Bob Johnson", "General Distress", "Assigned", "Officer Gibbs")
    )) }

    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate("Scanner") {
                launchSingleTop = true
            }
        }
    }

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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vistara Ranger",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF029602)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: "Dashboard"

            RangerBottomBar(
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    if (route == "Scanner") {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            navController.navigate("Scanner") {
                                launchSingleTop = true
                            }
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    } else {
                        navController.navigate(route) { launchSingleTop = true }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "Dashboard", Modifier.padding(innerPadding)) {
            composable("Dashboard") {
                RangerHomeContent(
                    sosAlerts = sosAlerts,
                    onUpdateAlert = { updatedAlert ->
                        sosAlerts = sosAlerts.map {
                            if (it.id == updatedAlert.id) updatedAlert else it
                        }
                    }
                )
            }
            composable("Scanner") { RangerScannerContent(onResult = { println("Scanned: $it") }) }
            composable("Alerts") {
                RangerAlertsContent(
                    sosAlerts = sosAlerts,
                    onUpdateAlert = { updatedAlert ->
                        sosAlerts = sosAlerts.map {
                            if (it.id == updatedAlert.id) updatedAlert else it
                        }
                    }
                )
            }
            composable("Profile") {
                RangerProfileScreen(
                    viewModel = rangerViewModel,
                    onLogoutSuccess = onLogoutSuccess,
                    onResetPasswordClick = onResetPasswordClick
                )
            }
        }
    }
}

@Composable
fun RangerHomeContent(
    sosAlerts: List<RangerSosAlert>,
    onUpdateAlert: (RangerSosAlert) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredAlerts = remember(sosAlerts, selectedFilter) {
        when (selectedFilter) {
            "Unresolved" -> sosAlerts.filter { it.status == "Unresolved" }
            "Assigned" -> sosAlerts.filter { it.status == "Assigned" }
            "Resolved" -> sosAlerts.filter { it.status == "Resolved" }
            else -> sosAlerts
        }
    }

    val unresolvedCount = remember(sosAlerts) {
        sosAlerts.count { it.status == "Unresolved" }
    }
    
    val assignedCount = remember(sosAlerts) {
        sosAlerts.count { it.status == "Assigned" }
    }

    val activeSosCount = unresolvedCount + assignedCount

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Welcome back,",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Officer Ranger",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Two Rows of Horizontal summary cards (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: No. of SOS Alerts
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSystemInDarkTheme()) Color(0xFF421E22) else Color(0xFFFFEBEE)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No. of SOS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$activeSosCount Active",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Red
                            )
                        }
                    }

                    // Card 2: Park Occupancy
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSystemInDarkTheme()) Color(0xFF1B3B2B) else Color(0xFFE8F5E9)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = Color(0xFF029602)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Occupancy",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "85% Full",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF029602)
                            )
                        }
                    }
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 3: Pending Alerts
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSystemInDarkTheme()) Color(0xFF352F1E) else Color(0xFFFFFDE7)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFBC02D)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pending Alerts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$unresolvedCount Pending",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFBC02D)
                            )
                        }
                    }

                    // Card 4: Assigned Responses
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSystemInDarkTheme()) Color(0xFF1E2E3A) else Color(0xFFE3F2FD)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentInd,
                                contentDescription = null,
                                tint = Color(0xFF2196F3)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Assigned Resp.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$assignedCount Assigned",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2196F3)
                            )
                        }
                    }
                }
            }
        }

        // SOS Alerts Management Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SOS Alerts Center",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF029602)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable filters (All, Unresolved, Assigned, Resolved)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("All", "Unresolved", "Assigned", "Resolved").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val chipBg = if (isSelected) Color(0xFF029602) else Color.White
                        val chipText = if (isSelected) Color.White else Color.Black
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(chipBg)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                color = chipText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // All filtered alerts
        if (filteredAlerts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No alerts found for: $selectedFilter", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredAlerts) { alert ->
                SosAlertCard(alert = alert, onUpdate = onUpdateAlert)
            }
        }
    }
}

@Composable
fun SosAlertCard(
    alert: RangerSosAlert,
    onUpdate: (RangerSosAlert) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = when (alert.status) {
        "Unresolved" -> if (isDark) Color(0xFF421E22) else Color.Red
        "Assigned" -> if (isDark) Color(0xFF3E3622) else Color(0xFFFFF8E1)
        else -> MaterialTheme.colorScheme.surface
    }

    val statusColor = when (alert.status) {
        "Unresolved" -> Color.Red
        "Assigned" -> Color(0xFFFFB300)
        else -> Color(0xFF029602)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = alert.visitorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color=Color.White
                    )
                    Text(
                        text = alert.alertType,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = alert.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Assignee details if assigned or resolved
            if (alert.assignedRanger != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentInd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Assigned to: ${alert.assignedRanger}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Ranger Action Buttons
            if (alert.status != "Resolved") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (alert.status == "Unresolved") {
                        Button(
                            onClick = {
                                onUpdate(alert.copy(status = "Assigned", assignedRanger = "Officer Ranger"))
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF029602)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Assign to Me", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else if (alert.status == "Assigned" && alert.assignedRanger == "Officer Ranger") {
                        Button(
                            onClick = {
                                onUpdate(alert.copy(status = "Resolved"))
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF029602)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark Resolved", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RangerAlertsContent(
    sosAlerts: List<RangerSosAlert>,
    onUpdateAlert: (RangerSosAlert) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredAlerts = remember(sosAlerts, selectedFilter) {
        when (selectedFilter) {
            "Unresolved" -> sosAlerts.filter { it.status == "Unresolved" }
            "Assigned" -> sosAlerts.filter { it.status == "Assigned" }
            "Resolved" -> sosAlerts.filter { it.status == "Resolved" }
            else -> sosAlerts
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("All", "Unresolved", "Assigned", "Resolved").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val chipBg = if (isSelected) Color(0xFF029602) else MaterialTheme.colorScheme.surfaceVariant
                    val chipText = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(chipBg)
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            color = chipText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (filteredAlerts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No alerts found for: $selectedFilter", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredAlerts) { alert ->
                SosAlertCard(alert = alert, onUpdate = onUpdateAlert)
            }
        }
    }
}