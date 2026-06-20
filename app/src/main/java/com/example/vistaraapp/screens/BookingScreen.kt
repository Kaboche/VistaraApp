package com.example.vistaraapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vistaraapp.database.ContactEvent
import com.example.vistaraapp.database.ContactState
import com.example.vistaraapp.entities_dataclass.allParks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    navController: NavController,
    parkId: Int,
    state: ContactState,
    onEvent: (ContactEvent) -> Unit
) {
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val lightGray = Color(0xFFF5F5F5)
    val darkGray = Color(0xFF333333)

    val park = allParks.find { it.id == parkId } ?: allParks[0]

    var validationError by remember { mutableStateOf<String?>(null) }

    // Anti-double-tap lock flag to stop backend unique reference collisions!
    var isButtonClicked by remember { mutableStateOf(false) }

    val currentAmount = state.amount

    // ✅ FIX 1: Declare the independent dialog phone number state at the root context level
    // This pre-fills it with the user's profile number by default, but allows separate editing!
    var stkPhoneNumber by remember { mutableStateOf("") }

    // Synchronize the dialog field with the profile number whenever the dialog is launched
    LaunchedEffect(state.showPaymentDialog) {
        if (state.showPaymentDialog) {
            stkPhoneNumber = state.phoneNumber
            isButtonClicked = false
        }
    }

    if (state.showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { if (!state.isBookingLoading) onEvent(ContactEvent.DismissPaymentDialog) },
            title = { Text("Confirm Payment", color = brandGreen, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Park: ${park.name}", fontWeight = FontWeight.Medium, color = Color.Gray)
                    Text("Total amount: KES $currentAmount", color = brandGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Check-in: ${state.checkInDate}", fontSize = 14.sp)
                    Text("Checkout: ${state.checkOutDate}", fontSize = 14.sp)
                    Text("Vehicle Reg: ${state.vehicleRegistration.ifEmpty { "None Specified" }}", fontSize = 14.sp)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray)

                    Text(
                        text = "Enter Phone Number for ${state.paymentMethod}:",
                        fontWeight = FontWeight.SemiBold,
                        color = darkGray,
                        fontSize = 14.sp
                    )

                    //  FIX 2: Point value and onValueChange to use the independent local variable safely
                    OutlinedTextField(
                        value = stkPhoneNumber,
                        onValueChange = { stkPhoneNumber = it },
                        placeholder = { Text("e.g. 254712345678") },
                        leadingIcon = { Icon(Icons.Filled.Phone, null, tint = brandGreen) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = brandGreen, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "An STK prompt will be sent to this phone number to complete the transaction.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isButtonClicked) {
                            isButtonClicked = true // Lock interface
                            //  FIX 3: Pass your isolated local number variable down to your ViewModel payload!
                            onEvent(ContactEvent.ConfirmBookingPayment(stkPhoneNumber))
                        }
                    },
                    // FIX 4: Validate against the typed dialog field instead of state.phoneNumber
                    enabled = !state.isBookingLoading && stkPhoneNumber.isNotBlank() && !isButtonClicked,
                    colors = ButtonDefaults.buttonColors(brandGreen)
                ) {
                    if (state.isBookingLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = pureWhite)
                    } else {
                        Text("Pay Now", color = pureWhite)
                    }
                }
            },
            dismissButton = {
                if (!state.isBookingLoading) {
                    TextButton({ onEvent(ContactEvent.DismissPaymentDialog) }) {
                        Text("Cancel", color = brandGreen)
                    }
                }
            }
        )
    }

    LaunchedEffect(state.isBookingSuccessful) {
        if (state.isBookingSuccessful) {
            onEvent(ContactEvent.DismissPaymentDialog)
        }
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
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isBookingSuccessful) {
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
                            Text("Group Size:", fontSize = 14.sp, color = darkGray, fontWeight = FontWeight.Medium)
                            Text("${state.groupSize}", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vehicle Reg:", fontSize = 14.sp, color = darkGray, fontWeight = FontWeight.Medium)
                            Text(state.vehicleRegistration.ifEmpty { "N/A" }, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Check-in date:", fontSize = 14.sp, color = darkGray, fontWeight = FontWeight.Medium)
                            Text(state.checkInDate, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Check-out date:", fontSize = 14.sp, color = darkGray, fontWeight = FontWeight.Medium)
                            Text(state.checkOutDate, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount:", fontSize = 16.sp, color = brandGreen, fontWeight = FontWeight.Bold)
                            Text("KES $currentAmount", fontSize = 16.sp, color = brandGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate("check_in") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(35.dp),
                    colors = ButtonDefaults.buttonColors(brandGreen)
                ) {
                    Text("CHECK IN NOW", fontWeight = FontWeight.Bold, color = pureWhite)
                }
            } else {
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

                        // Check-in Date
                        OutlinedTextField(
                            value = state.checkInDate,
                            onValueChange = { onEvent(ContactEvent.EnteredCheckInDate(it)) },
                            label = { Text("Check-in Date") },
                            placeholder = { Text("YYYY-MM-DD") },
                            leadingIcon = { Icon(Icons.Filled.DateRange, null, tint = brandGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Check-out Date
                        OutlinedTextField(
                            value = state.checkOutDate,
                            onValueChange = { onEvent(ContactEvent.EnteredCheckOutDate(it)) },
                            label = { Text("Check-out Date") },
                            placeholder = { Text("YYYY-MM-DD") },
                            leadingIcon = { Icon(Icons.Filled.DateRange, null, tint = brandGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Group Size
                        OutlinedTextField(
                            value = if (state.groupSize == 0) "" else state.groupSize.toString(),
                            onValueChange = {
                                val parsedInt = it.toIntOrNull() ?: 0
                                onEvent(ContactEvent.EnteredGroupSize(parsedInt))
                                onEvent(ContactEvent.EnteredAmount(parsedInt * 1.0))
                            },
                            label = { Text("Number of People") },
                            leadingIcon = { Icon(Icons.Filled.Person, null, tint = brandGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Vehicle Registration
                        OutlinedTextField(
                            value = state.vehicleRegistration,
                            onValueChange = { onEvent(ContactEvent.EnteredVehicleRegistration(it)) },
                            label = { Text("Vehicle Registration") },
                            placeholder = { Text("e.g. KBC 123A") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Total amount: KES $currentAmount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = brandGreen)

                        Text("Select Payment Method", fontWeight = FontWeight.Medium, color = brandGreen)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            FilterChip(
                                selected = state.paymentMethod == "eCitizen",
                                onClick = { onEvent(ContactEvent.EnteredPaymentMethod("eCitizen")) },
                                label = { Text("eCitizen") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.DarkGray, selectedLabelColor = pureWhite
                                )
                            )
                            FilterChip(
                                selected = state.paymentMethod == "MPESA",
                                onClick = { onEvent(ContactEvent.EnteredPaymentMethod("MPESA")) },
                                label = { Text("M-Pesa") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = brandGreen, selectedLabelColor = pureWhite
                                )
                            )
                        }

                        val finalErrorMessage = validationError ?: state.bookingErrorMessage
                        if (finalErrorMessage != null) {
                            Text(finalErrorMessage, color = Color.Red, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (state.checkInDate.isBlank() || state.checkOutDate.isBlank()) {
                                    validationError = "Please complete both date fields"
                                } else if (state.groupSize < 1) {
                                    validationError = "Enter valid number of people"
                                } else if (state.vehicleRegistration.isBlank()) {
                                    validationError = "Vehicle registration is required"
                                } else if (state.paymentMethod.isBlank()) {
                                    validationError = "Please choose a payment method"
                                } else {
                                    validationError = null
                                    onEvent(ContactEvent.CreateBooking)
                                }
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