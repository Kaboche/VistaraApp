package com.example.vistaraapp.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val lightGray= Color(0xFFF5F5F5)

    var groupSize by remember { mutableStateOf("1") }
    var vehicleNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGray)

    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Check In to Park", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = brandGreen)
        Spacer(Modifier.height(8.dp))
        Text("Please provide your details", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(48.dp))

        OutlinedTextField(
            value = groupSize,
            onValueChange = { groupSize = it },
            label = { Text("Group Size") },
            placeholder = { Text("Number of people") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = brandGreen,
                focusedLabelColor = brandGreen,
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = vehicleNumber,
            onValueChange = { vehicleNumber = it },
            label = { Text("Vehicle Number (Optional)") },
            placeholder = { Text("e.g., KAA 123B") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = brandGreen,
                focusedLabelColor = brandGreen,
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        if (errorMessage != null) {
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            )
            Text(errorMessage!!, color = Color(0xFFD32F2F), fontSize = 12.sp)
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                val size = groupSize.toIntOrNull()
                if (size == null || size < 1) {
                    errorMessage = "Please enter a valid group size"
                } else {
                    isLoading = true
                    isLoading = false
                    navController.navigate("map_tracking")
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = brandGreen,
                disabledContainerColor = brandGreen.copy(alpha = 0.5f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp), color = pureWhite)
            } else {
                Text("CONFIRM CHECK-IN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = pureWhite)
            }
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancel", color = brandGreen, fontSize = 14.sp)
        }
    }
}