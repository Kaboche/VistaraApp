package com.example.vistaraapp

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri // Added for .toUri() extension
import com.example.vistaraapp.ui.theme.PureWhite
import com.example.vistaraapp.ui.theme.VistaraTheme

data class EmergencyType(val label: String, val icon: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyInfoCard(
    onSendEmergencyReport: (type: String, details: String) -> Unit,
    modifier: Modifier = Modifier,
    brandGreen: Color = Color(0xFF029602) // Now properly utilized below
) {
    val context = LocalContext.current
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf("") }
    var additionalDetails by remember { mutableStateOf("") }

    val emergencyCategories = remember {
        listOf(
            EmergencyType("Animal Attack", "🐾"),
            EmergencyType("Medical Crisis", "🚑"),
            EmergencyType("Wildfire", "🔥"),
            EmergencyType("Poaching/Crime", "🚨"),
            EmergencyType("Lost / Stranded", "📍"),
            EmergencyType("Vehicle Break", "🛠️")
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Emergency & Safety",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFD32F2F).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Emergency Contacts",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.height(8.dp))

            EmergencyContactRow(icon = "", label = "Park Rangers", number = "0700 597 000", textColor = brandGreen) {
                context.startActivity(Intent(Intent.ACTION_DIAL, "tel:0700597000".toUri()))
            }
            EmergencyContactRow(icon ="", label = "Ambulance", number = "999", textColor = brandGreen) {
                context.startActivity(Intent(Intent.ACTION_DIAL, "tel:999".toUri()))
            }
            EmergencyContactRow(icon ="", label = "Police", number = "112", textColor = brandGreen) {
                context.startActivity(Intent(Intent.ACTION_DIAL, "tel:112".toUri()))
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { isBottomSheetOpen = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.Filled.Warning, contentDescription = "SOS", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SOS EMERGENCY REPORT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "💡 Safety Tips", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFE65100))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "• Stay inside your vehicle at all times", fontSize = 11.sp, color = Color.DarkGray)
            Text(text = "• Keep a safe distance from animals", fontSize = 11.sp, color = Color.DarkGray)
        }
    }

    if (isBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isBottomSheetOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Report Emergency Incident",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
                Text(
                    text = "Select a category to speed up field-ranger response.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(140.dp)
                ) {
                    items(emergencyCategories) { item ->
                        val isSelected = selectedType == item.label
                        OutlinedCard(
                            onClick = { selectedType = item.label },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFFD32F2F) else Color.LightGray
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) Color(0xFFD32F2F).copy(alpha = 0.05f) else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(item.icon, fontSize = 18.sp)
                                Text(item.label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = additionalDetails,
                    onValueChange = { additionalDetails = it }, // Fixed compiler error
                    label = { Text("Describe the situation (optional)") },
                    placeholder = { Text("e.g., Elephant blocking trail, flat tire") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { isBottomSheetOpen = false },
                        modifier = Modifier.weight(1f), // Fixed '1s' compiler error
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (selectedType.isNotEmpty()) {
                                onSendEmergencyReport(selectedType, additionalDetails)
                                isBottomSheetOpen = false
                            }
                        },
                        enabled = selectedType.isNotEmpty(),
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("SUBMIT SOS ALERT", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun EmergencyContactRow(
    icon: String,
    label: String,
    number: String,
    textColor: Color, // Accept parameter to fix unused warning
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(icon, fontSize = 14.sp)
            Text(text = label, fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
        }
        Text(text = number, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyInfoCardPreview() {
    VistaraTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            EmergencyInfoCard(
                onSendEmergencyReport = { _, _ -> }
            )
        }
    }
}
