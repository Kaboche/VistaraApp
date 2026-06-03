package com.example.vistaraapp

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Booking(
    val id: String,
    val visitDate: String,
    val numberOfPeople: Int,
    val totalAmount: Int,
    val status: BookingStatus
)

enum class BookingStatus {
    UPCOMING, COMPLETED, CANCELLED
}

// Sample bookings for Nairobi National Park
val sampleBookings = listOf(
    Booking(
        id = "NBO-001",
        visitDate = "June 15, 2026",
        numberOfPeople = 2,
        totalAmount = 1000,
        status = BookingStatus.UPCOMING
    ),
    Booking(
        id = "NBO-002",
        visitDate = "May 10, 2026",
        numberOfPeople = 4,
        totalAmount = 2000,
        status = BookingStatus.COMPLETED
    )
)

@Composable
fun BookingsScreen(navController: NavController) {
    val brandGreen = Color(0xFF029602)
    val lightGray = Color(0xFFF8F9FA)
    val parkName = "Nairobi National Park"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightGray
    ) {
        if (sampleBookings.isEmpty()) {
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

                val upcomingBookings = sampleBookings.filter { it.status == BookingStatus.UPCOMING }
                val completedBookings = sampleBookings.filter { it.status == BookingStatus.COMPLETED }

                if (upcomingBookings.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Upcoming Visits", brandGreen = brandGreen)
                    }
                    items(upcomingBookings) { booking ->
                        BookingCard(
                            booking = booking,
                            parkName = parkName,
                            onCancel = { /* Handle cancellation */ },
                            brandGreen = brandGreen
                        )
                    }
                }

                if (completedBookings.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Past Visits", brandGreen = brandGreen)
                    }
                    items(completedBookings) { booking ->
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
    booking: Booking,
    parkName: String,
    onCancel: (() -> Unit)?,
    brandGreen: Color
) {
    val statusColor = when (booking.status) {
        BookingStatus.UPCOMING -> Color(0xFF4CAF50)
        BookingStatus.COMPLETED -> Color(0xFF9E9E9E)
        BookingStatus.CANCELLED -> Color(0xFFF44336)
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
                        text = booking.status.name,
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

            DetailRow(label = "Visit Date", value = booking.visitDate)
            DetailRow(label = "People", value = "${booking.numberOfPeople} person(s)")
            DetailRow(label = "Total Amount", value = "KES ${booking.totalAmount}", valueColor = brandGreen)
            DetailRow(label = "Booking ID", value = booking.id)

            // Only show Cancel button for upcoming bookings
            if (booking.status == BookingStatus.UPCOMING && onCancel != null) {
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

