package com.example.vistaraapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CheckInScreen(navController: NavController) {
    // Brand Colors
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)

    // State variables
    var groupSize by remember { mutableStateOf("1") }
    var vehicleNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "Check In to Park",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = brandGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please provide your details",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Group Size Field
        OutlinedTextField(
            value = groupSize,
            onValueChange = { groupSize = it },
            label = { Text("Group Size") },
            placeholder = { Text("Number of people") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = brandGreen,
                focusedLabelColor = brandGreen,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Number Field (Optional)
        OutlinedTextField(
            value = vehicleNumber,
            onValueChange = { vehicleNumber = it },
            label = { Text("Vehicle Number (Optional)") },
            placeholder = { Text("e.g., KAA 123B") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = brandGreen,
                focusedLabelColor = brandGreen,
                unfocusedBorderColor = Color.LightGray
            )
        )

        // Error Message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = Color(0xFFD32F2F),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Confirm Button
        Button(
            onClick = {
                val size = groupSize.toIntOrNull()
                if (size == null || size < 1) {
                    errorMessage = "Please enter a valid group size"
                } else {
                    isLoading = true
                    // Simulate check-in process
                    // In real app, call repository.checkIn() here
                    isLoading = false
                    // Navigate back to home
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = brandGreen,
                disabledContainerColor = brandGreen.copy(alpha = 0.5f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = pureWhite
                )
            } else {
                Text(
                    text = "CONFIRM CHECK-IN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = pureWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cancel Button
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text(
                text = "Cancel",
                color = brandGreen,
                fontSize = 14.sp
            )
        }
    }
}