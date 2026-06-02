package com.example.vistaraapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmergencyInfoCard(
    onSOSClick: () -> Unit,
    brandGreen: Color = Color(0xFF029602)
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚨", fontSize = 24.sp)
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

            // Emergency Contacts Section
            Text(
                text = "Emergency Contacts",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Park Rangers
            EmergencyContactRow(
                icon = "📞",
                label = "Park Rangers",
                number = "0800 597 000",
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:0800597000")
                    }
                    context.startActivity(intent)
                }
            )

            // Ambulance
            EmergencyContactRow(
                icon = "🚑",
                label = "Ambulance",
                number = "999",
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:999")
                    }
                    context.startActivity(intent)
                }
            )

            // Police
            EmergencyContactRow(
                icon = "👮",
                label = "Police",
                number = "112",
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:112")
                    }
                    context.startActivity(intent)
                }
            )

            // Fire Brigade
            EmergencyContactRow(
                icon = "🔥",
                label = "Fire Brigade",
                number = "999",
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:999")
                    }
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(12.dp))

            // SOS Button
            Button(
                onClick = onSOSClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.Filled.Warning, contentDescription = "SOS", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SOS EMERGENCY ALERT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Safety Tips Preview
            Text(
                text = "💡 Safety Tips",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE65100)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• Stay inside your vehicle at all times",
                fontSize = 11.sp,
                color = Color.DarkGray
            )
            Text(
                text = "• Keep doors locked and windows up",
                fontSize = 11.sp,
                color = Color.DarkGray
            )
            Text(
                text = "• Keep a safe distance from animals",
                fontSize = 11.sp,
                color = Color.DarkGray
            )
            Text(
                text = "• Don't feed or approach wildlife",
                fontSize = 11.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun EmergencyContactRow(
    icon: String,
    label: String,
    number: String,
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
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = number,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF029602)
        )
    }
}