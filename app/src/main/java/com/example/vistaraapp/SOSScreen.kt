package com.example.vistaraapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(navController: NavController) {
    var showConfirmation by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val errorRed = Color(0xFFD32F2F)

    // Success dialog
    if (showSuccess) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = pureWhite)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.CheckCircle, null, Modifier.size(64.dp), tint = brandGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Alert Sent!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = brandGreen)
                    Text("Help is on the way. Rangers have been notified.", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.popBackStack() }, shape = RoundedCornerShape(40.dp)) {
                        Text("OK", color = pureWhite)
                    }
                }
            }
        }
    }

    // Confirmation dialog
    if (showConfirmation && selectedType != null) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("🚨 Send SOS Alert?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = errorRed) },
            text = {
                Column {
                    Text("Emergency Type: $selectedType")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Your current location will be sent to park rangers immediately.", fontSize = 14.sp, color = Color.Gray)
                    Text("⚠️ Only use this feature in real emergencies.", fontSize = 12.sp, color = errorRed)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmation = false
                        isLoading = true
                        // Simulate sending
                        isLoading = false
                        showSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = errorRed)
                ) {
                    if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = pureWhite)
                    else Text("YES, SEND ALERT", color = pureWhite)
                }
            },
            dismissButton = { TextButton(onClick = { showConfirmation = false }) { Text("NO, CANCEL", color = brandGreen) } }
        )
    }

    // Main screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SOS Emergency", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = brandGreen) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = brandGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = pureWhite)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(Modifier.size(80.dp), shape = RoundedCornerShape(40.dp), colors = CardDefaults.cardColors(containerColor = errorRed.copy(0.1f))) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Warning, null, Modifier.size(48.dp), tint = errorRed)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("EMERGENCY SOS", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = errorRed)
            Text("Select the type of emergency", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))

            SOSButtonEmoji(emoji = "🧭", text = "LOST", desc = "I am lost in the park", color = Color(0xFFEF4444)) { selectedType = "LOST"; showConfirmation = true }
            SOSButtonEmoji(emoji = "🏥", text = "MEDICAL", desc = "Medical emergency", color = Color(0xFFF59E0B)) { selectedType = "MEDICAL"; showConfirmation = true }
            SOSButtonEmoji(emoji = "🐘", text = "WILDLIFE", desc = "Animal threat", color = Color(0xFF8B5CF6)) { selectedType = "WILDLIFE"; showConfirmation = true }
            SOSButtonEmoji(emoji = "📞", text = "OTHER", desc = "Other emergency", color = Color(0xFF6B7280)) { selectedType = "OTHER"; showConfirmation = true }
        }
    }
}

@Composable
fun SOSButtonEmoji(emoji: String, text: String, desc: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 32.sp)
            Spacer(Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(desc, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}