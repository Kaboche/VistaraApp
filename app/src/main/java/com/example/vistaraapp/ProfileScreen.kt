package com.example.vistaraapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.compose.rememberNavController
import com.example.vistaraapp.ui.theme.VistaraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController
) {
    // Mock user data
    val userName = "Kaboche"
    val userIdNumber = "49283489"
    val userPhone = "+254732541948"
    val userVehicle = "KAA 123B"
    val memberSince = "2025"

    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val lightGray = Color(0xFFF5F5F5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = pureWhite
                )
            )
        },
        containerColor = pureWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Card(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(containerColor = brandGreen)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(60.dp),
                        tint = pureWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = brandGreen
            )

            Text(
                text = "Member since $memberSince",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Info Card - Text only, no icons
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = lightGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Full Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Full Name", color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text(userName, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

                    // ID Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ID Number", color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text(userIdNumber, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

                    // Phone Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Phone Number", color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text(userPhone, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

                    // Vehicle Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vehicle Number", color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text(userVehicle, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button - FIXED SYNTAX
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(35.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F),
                    contentColor = pureWhite
                )
            ) {
                Icon(
                    Icons.Filled.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOGOUT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val dummyNavController = rememberNavController()
    VistaraTheme {
        ProfileScreen(navController = dummyNavController)
    }
}