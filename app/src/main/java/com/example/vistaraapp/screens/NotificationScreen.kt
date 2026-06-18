package com.example.vistaraapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// 📋 Notification Data Structure
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isAlert: Boolean = false
)

// 📍 Hardcoded Safari Data for testing layout look & feel
val sampleNotifications = listOf(
    NotificationItem(
        id = "1",
        title = "Gate Check-in Confirmed",
        message = "Your digital pass for Nairobi National Park has been successfully verified at the main gate. Enjoy your game drive!",
        timestamp = "2 hours ago"
    ),
    NotificationItem(
        id = "2",
        title = "Weather Alert: Rain Storm",
        message = "Sudden heavy downpours are expected near the central savannah tracks around 3:00 PM. Drive cautiously.",
        timestamp = "4 hours ago",
        isAlert = true
    ),
    NotificationItem(
        id = "3",
        title = "Booking Successful",
        message = "Payment received! Booking ID NBO-001 is now confirmed for June 15, 2026.",
        timestamp = "1 day ago"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val brandGreen = Color(0xFF029602)
    val lightGray = Color(0xFFF8F9FA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notifications", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                // ↩️ Responsive Back Arrow navigation
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = brandGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = lightGray
        ) {
            if (sampleNotifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No alerts or notifications yet.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sampleNotifications) { notification ->
                        NotificationCard(notification = notification, brandGreen = brandGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem, brandGreen: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 🟢 Status Indicator Dot (Red for urgent weather alerts, Green for standard updates)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .offset(y = 4.dp)
                    .background(
                        color = if (notification.isAlert) Color(0xFFF44336) else brandGreen,
                        shape = CircleShape
                    )
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = notification.timestamp,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}