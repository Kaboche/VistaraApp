package com.example.vistaraapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun BookingDetailScreen(navController: NavController, bookingId: String) {
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)

    val booking = sampleBookings.find { it.id == bookingId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booking Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = brandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = pureWhite)
            )
        }
    ) { paddingValues ->
        if (booking == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❌", fontSize = 48.sp)
                    Text("Booking Not Found", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (booking.status) {
                            BookingStatus.UPCOMING -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            BookingStatus.COMPLETED -> Color(0xFF9E9E9E).copy(alpha = 0.1f)
                            BookingStatus.CANCELLED -> Color(0xFFF44336).copy(alpha = 0.1f)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status: ${booking.status.name}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (booking.status) {
                                BookingStatus.UPCOMING -> Color(0xFF4CAF50)
                                BookingStatus.COMPLETED -> Color(0xFF9E9E9E)
                                BookingStatus.CANCELLED -> Color(0xFFF44336)
                            }
                        )
                        if (booking.status == BookingStatus.UPCOMING) {
                            TextButton(onClick = { /* Cancel logic */ }) {
                                Text("Cancel", color = Color(0xFFF44336))
                            }
                        }
                    }
                }

                // Booking Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Booking Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandGreen
                        )
                        DetailRow(label = "Booking ID", value = booking.id)
                        DetailRow(label = "Visit Date", value = booking.visitDate)
                        DetailRow(label = "Number of People", value = "${booking.numberOfPeople}")
                        DetailRow(label = "Total Amount", value = "KES ${booking.totalAmount}", valueColor = brandGreen)
                    }
                }

                // Back Button
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) {
                    Text("Back to Bookings", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}