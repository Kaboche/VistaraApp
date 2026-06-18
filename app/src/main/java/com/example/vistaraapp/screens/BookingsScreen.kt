package com.example.vistaraapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.vistaraapp.api_requests_responses.BookingData

@Composable
fun BookingsScreen(
    navController: NavController,
    viewModel: BookingViewModel, // Wired to accept the injected ViewModel
    authToken: String            // Wired to accept your secure JWT Token
) {
    val brandGreen = Color(0xFF029602)
    val lightGray = Color(0xFFF8F9FA)
    val parkName = "Nairobi National Park"

    // 1. Trigger the network API fetch as soon as this screen enters the composition
    LaunchedEffect(key1 = authToken) {
        viewModel.fetchBookings(authToken)
    }

    // 2. Read the current architectural state from our ViewModel
    val uiState by viewModel.uiState

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightGray
    ) {
        when (val state = uiState) {
            is BookingUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = brandGreen)
                }
            }
            is BookingUiState.Error -> {
                ErrorState(message = state.message, onRetry = { viewModel.fetchBookings(authToken) }, brandGreen = brandGreen)
            }
            is BookingUiState.Success -> {
                val networkBookings = state.bookings

                if (networkBookings.isEmpty()) {
                    EmptyBookingsState(navController, brandGreen, parkName)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = parkName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = brandGreen,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Split bookings into Upcoming and Past based on your actual backend paymentStatus or bookingStatus
                        val upcomingBookings = networkBookings.filter { it.bookingStatus == "CONFIRMED" || it.bookingStatus == "PENDING" }
                        val pastBookings = networkBookings.filter { it.bookingStatus == "COMPLETED" || it.bookingStatus == "CANCELLED" }

                        if (upcomingBookings.isNotEmpty()) {
                            item { SectionHeader(title = "Upcoming Visits", brandGreen = brandGreen) }
                            items(upcomingBookings) { booking ->
                                BookingCard(
                                    booking = booking,
                                    parkName = parkName,
                                    onCancel = { /* Handle cancellation later */ },
                                    brandGreen = brandGreen
                                )
                            }
                        }

                        if (pastBookings.isNotEmpty()) {
                            item { SectionHeader(title = "Past Visits", brandGreen = brandGreen) }
                            items(pastBookings) { booking ->
                                BookingCard(
                                    booking = booking,
                                    parkName = parkName,
                                    onCancel = null,
                                    brandGreen = brandGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, brandGreen: Color) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = brandGreen,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun BookingCard(
    booking: BookingData, // Re-mapped to use your live network model
    parkName: String,
    onCancel: (() -> Unit)?,
    brandGreen: Color
) {
    // Dynamically choose colors based on strings sent back from the backend API
    val statusColor = when (booking.bookingStatus.uppercase()) {
        "CONFIRMED", "COMPLETED" -> Color(0xFF4CAF50)
        "PENDING" -> Color(0xFFFF9800)
        else -> Color(0xFFF44336) // CANCELLED
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = parkName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = booking.bookingStatus,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))

            DetailRow(label = "Check In Date", value = booking.checkInDate)
            DetailRow(label = "Check Out Date", value = booking.checkOutDate)
            DetailRow(label = "Vehicle Reg", value = booking.vehicleRegistration)
            DetailRow(label = "Group Size", value = "${booking.groupSize} person(s)")
            DetailRow(label = "Total Amount", value = "KES ${booking.amount}", valueColor = brandGreen)
            DetailRow(label = "Booking Ref", value = booking.bookingReference)

            if ((booking.bookingStatus == "CONFIRMED" || booking.bookingStatus == "PENDING") && onCancel != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Cancel Booking", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
fun EmptyBookingsState(navController: NavController, brandGreen: Color, parkName: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Bookings Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = brandGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You haven't booked a safari at $parkName yet.",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { navController.navigate("wildlife") },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
        ) {
            Text("Book Now", color = Color.White)
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit, brandGreen: Color) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Something Went Wrong", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Retry", color = Color.White)
        }
    }
}