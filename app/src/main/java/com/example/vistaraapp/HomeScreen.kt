package com.example.vistaraapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Brand Colors
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)

    var isCheckedIn by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Vistara",
                        color = brandGreen,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = pureWhite
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = brandGreen
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = brandGreen.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Welcome to Vistara",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandGreen
                    )
                    Text(
                        "Park Safety System",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCheckedIn) brandGreen else Color(0xFFFF9800)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isCheckedIn) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = pureWhite,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (isCheckedIn) "Currently in Park" else "Not Checked In",
                            color = pureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isCheckedIn) "Your safety is our priority" else "Please check in to continue",
                            color = pureWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Check In/Out Button
            Button(
                onClick = {
                    if (isCheckedIn) {
                        isCheckedIn = false
                    } else {
                        navController.navigate("checkin")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCheckedIn) Color(0xFFF44336) else brandGreen
                )
            ) {
                Icon(
                    if (isCheckedIn) Icons.Filled.ExitToApp else Icons.Filled.ArrowForward,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCheckedIn) "CHECK OUT" else "CHECK IN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = pureWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Map Button - Using Place icon (location pin)
            OutlinedButton(
                onClick = { /* TODO: Map feature will be added later */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = brandGreen
                )
            ) {
                Text("🗺️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "VIEW PARK MAP",
                    fontSize = 16.sp,
                    color = brandGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // SOS EMERGENCY BUTTON
            Button(
                onClick = { navController.navigate("sos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("🚨", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "SOS EMERGENCY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = pureWhite
                )
            }
        }
    }
}