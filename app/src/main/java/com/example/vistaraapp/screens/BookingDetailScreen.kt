package com.example.vistaraapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vistaraapp.viewmodels.BookingUiState
import com.example.vistaraapp.viewmodels.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    navController: NavController,
    bookingReference: String, // Matching your API's tracking field
    viewModel: BookingViewModel
) {
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)

    // Extract the live state directly from the shared ViewModel
    val uiState by viewModel.uiState

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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Fixed deprecation warning
                            contentDescription = "Back",
                            tint = brandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = pureWhite)
            )
        }
    ) { paddingValues ->
        // Handle matching UI elements based on current global network state
        when (val state = uiState) {
            is BookingUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = brandGreen)
                }
            }
            is BookingUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red, fontSize = 14.sp)
                }
            }
            is BookingUiState.Success -> {
                // Find the target booking item from our cached network array list
                val booking = state.bookings.find { it.bookingReference == bookingReference }

                if (booking == null) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("❌", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Booking Not Found", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                            ) {
                                Text("Go Back", color = Color.White)
                            }
                        }
                    }
                } else {
                    // Match live API status colors safely using explicit Strings
                    val status = booking.bookingStatus ?: "PENDING"
                    val statusColor = when (status.uppercase()) {
                        "CONFIRMED", "COMPLETED" -> Color(0xFF4CAF50)
                        "PENDING" -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336) // CANCELLED
                    }

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
                            colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status: $status",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                                if (status == "CONFIRMED" || status == "PENDING") {
                                    TextButton(onClick = { /* Handle cancellation network request */ }) {
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
                                DetailRow(label = "Booking Ref", value = booking.bookingReference)
                                DetailRow(label = "Check In Date", value = booking.checkInDate)
                                DetailRow(label = "Check Out Date", value = booking.checkOutDate)
                                DetailRow(label = "Vehicle Registration", value = booking.vehicleRegistration)
                                DetailRow(label = "Group Size", value = "${booking.groupSize ?: 0} person(s)")
                                DetailRow(
                                    label = "Total Amount",
                                    value = "KES ${booking.amount ?: 0.0}",
                                    valueColor = brandGreen
                                )
                            }
                        }

                        // Back Button
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                        ) {
                            Text("Back to Bookings", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}