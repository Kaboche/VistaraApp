package com.example.vistaraapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.vistaraapp.ui.theme.VistaraTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navController: NavController, parkId: Int) {
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val lightGray = Color(0xFFF5F5F5)
    val darkGray = Color(0xFF333333)
    val scope = rememberCoroutineScope()

    val park = allParks.find { it.id == parkId } ?: allParks[0]

    // ✅ FIXED: Both check-in and checkout variables are correctly declared as Strings
    var chekinDate by remember { mutableStateOf("") }
    var checkoutDate by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("1") }
    var selectedPaymentMethod by remember { mutableStateOf("M-pesa") }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentSuccess by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val numberOfPeople = people.toIntOrNull() ?: 1
    val totalAmount = numberOfPeople * 500

    // Payment confirmation dialog
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Confirm Payment", color = brandGreen) },
            text = {
                Column {
                    Text("Payment method: $selectedPaymentMethod")
                    Text("Total amount: KES $totalAmount")
                    Text("Park: ${park.name}")
                    Text("Check-in: $chekinDate")
                    Text("Checkout: $checkoutDate")
                    Spacer(Modifier.height(8.dp))
                    Text("Proceed to pay via $selectedPaymentMethod?", color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isProcessingPayment = true
                        scope.launch {
                            delay(2000)
                            isProcessingPayment = false
                            showPaymentDialog = false
                            paymentSuccess = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(brandGreen)
                ) {
                    if (isProcessingPayment) CircularProgressIndicator(Modifier.size(20.dp), color = pureWhite)
                    else Text("Pay Now", color = pureWhite)
                }
            },
            dismissButton = {
                TextButton({ showPaymentDialog = false }) {
                    Text("Cancel", color = brandGreen)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Your Visit", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = brandGreen) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = brandGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = pureWhite)
            )
        },
        containerColor = lightGray
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (paymentSuccess) {
                // Success card
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = brandGreen.copy(alpha = 0.1f))
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Payment Successful!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = brandGreen)
                        Text("Your booking has been confirmed.", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Booking Details Summary Card
                Text(
                    text = "Booking Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = brandGreen,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = pureWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Park:", fontSize = 14.sp, color = darkGray, fontWeight = FontWeight.Medium)
                            Text(park.name, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Number of People:", fontSize = 14.sp, color = darkGray, fontWeight = FontWeight.Medium)
                            Text("$numberOfPeople", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Check-in date:", fontSize = 14.sp, color = darkGray, fontWeight = FontWeight.Medium)
                            Text(chekinDate, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Checkout date:", fontSize = 14.sp, color = darkGray, fontWeight = FontWeight.Medium)
                            Text(checkoutDate, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount:", fontSize = 16.sp, color = brandGreen, fontWeight = FontWeight.Bold)
                            Text("KES $totalAmount", fontSize = 16.sp, color = brandGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        navController.navigate("checkin")
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(35.dp),
                    colors = ButtonDefaults.buttonColors(brandGreen)
                ) {
                    Spacer(Modifier.width(8.dp))
                    Text("CHECK IN NOW", fontWeight = FontWeight.Bold, color = pureWhite)
                }
            } else {
                // Booking Form (before payment)
                Text(park.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = brandGreen)
                Text(park.location, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(pureWhite)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("About this park", fontWeight = FontWeight.Bold, color = brandGreen)
                        Text(park.description, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
                Spacer(Modifier.height(24.dp))

                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(pureWhite)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Booking Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = brandGreen)

                        // --- CHECK-IN DATE FIELD ---
                        OutlinedTextField(
                            value = chekinDate,
                            onValueChange = { chekinDate = it },
                            label = { Text("Checkin Date") },
                            placeholder = { Text("YYYY-MM-DD") },
                            leadingIcon = { Icon(Icons.Filled.DateRange, null, tint = brandGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // ✅ FIXED: Changed to Text for hyphens
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        // --- CHECKOUT DATE FIELD ---
                        OutlinedTextField(
                            value = checkoutDate,
                            onValueChange = { checkoutDate = it },
                            label = { Text("Checkout Date") },
                            placeholder = { Text("YYYY-MM-DD") },
                            leadingIcon = { Icon(Icons.Filled.DateRange, null, tint = brandGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        // --- NUMBER OF PEOPLE FIELD ---
                        OutlinedTextField(
                            value = people,
                            onValueChange = { people = it },
                            label = { Text("Number of People") },
                            leadingIcon = { Icon(Icons.Filled.Person, null, tint = brandGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Text("Total amount: KES $totalAmount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = brandGreen)

                        Text("Select Payment Method", fontWeight = FontWeight.Medium, color = brandGreen)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            FilterChip(
                                selected = selectedPaymentMethod == "eCitizen",
                                onClick = { selectedPaymentMethod = "eCitizen" },
                                label = { Text("eCitizen") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.DarkGray,
                                    selectedLabelColor = pureWhite
                                )
                            )
                            FilterChip(
                                selected = selectedPaymentMethod == "M-Pesa",
                                onClick = { selectedPaymentMethod = "M-Pesa" },
                                label = { Text("M-Pesa") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = brandGreen,
                                    selectedLabelColor = pureWhite
                                )
                            )
                        }

                        if (error != null) Text(error!!, color = Color.Red, fontSize = 12.sp)

                        Button(
                            onClick = {
                                if (chekinDate.isBlank() || checkoutDate.isBlank()) error = "Please complete both date fields"
                                else if (people.toIntOrNull() == null || people.toInt() < 1) error = "Enter valid number of people"
                                else { error = null; showPaymentDialog = true }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(brandGreen)
                        ) {
                            Text("PROCEED TO PAYMENT", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ========== PREVIEWS ==========

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Booking Form")
@Composable
fun BookingScreenPreview() {
    val dummyNavController = rememberNavController()
    VistaraTheme {
        BookingScreen(navController = dummyNavController, parkId = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "After Payment - CHECK IN Button")
@Composable
fun AfterPaymentPreview() {
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val darkGray = Color(0xFF333333)
    val park = allParks[0]

    VistaraTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Book Your Visit", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = brandGreen) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = pureWhite)
                )
            },
            containerColor = Color(0xFFF5F5F5)
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = brandGreen.copy(alpha = 0.1f))
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Payment Successful!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = brandGreen)
                        Text("Your booking has been confirmed.", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(Modifier.height(32.dp))

                Text("Booking Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = brandGreen)

                Spacer(Modifier.height(8.dp))

                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = pureWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Park:", color = darkGray)
                            Text(park.name, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Date:", color = darkGray)
                            Text("2025-12-25", color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("People:", color = darkGray)
                            Text("2", color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }

                        HorizontalDivider(color = Color.LightGray)

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total:", color = brandGreen, fontWeight = FontWeight.Bold)
                            Text("KES 1000", color = brandGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(brandGreen)
                ) {
                    Text("CHECK IN NOW", fontWeight = FontWeight.Bold, color = pureWhite)
                }
            }
        }
    }
}