package com.example.vistaraapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vistaraapp.api.RetrofitClient
import com.example.vistaraapp.database.ContactEvent
import com.example.vistaraapp.database.ContactState
import com.example.vistaraapp.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    state: ContactState,
    onEvent: (ContactEvent) -> Unit,
    authToken: String
) {
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val lightGrayLabel = Color(0x99000000)
    val dividerColor=Color(0xFFE0E0E0)
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    var isSyncing by remember { mutableStateOf(false) }

    // Fetch profile
    LaunchedEffect(authToken) {
        if (authToken.isNotEmpty()) {
            try {
                isSyncing = true

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.profileInstance.getProfileDetails("Bearer $authToken")
                }

                if (response.isSuccessful) {
                    val rawBody = response.body() as? Map<*, *>
                    val dataMap = rawBody?.get("data") as? Map<*, *>

                    dataMap?.let {
                        onEvent(ContactEvent.SetFullName((it["fullName"] ?: "").toString()))
                        onEvent(ContactEvent.SetEmail((it["email"] ?: "").toString()))
                        onEvent(ContactEvent.SetPhoneNumber((it["phoneNumber"] ?: "").toString()))
                        onEvent(ContactEvent.SetEmergencyNumber((it["emergencyContactPhone"] ?: "").toString()))
                        onEvent(ContactEvent.SaveContact)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSyncing = false
            }
        }
    }

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
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = brandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = pureWhite)
            )
        },
        containerColor = pureWhite
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = brandGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(brandGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = pureWhite,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(pureWhite, CircleShape)
                        .clickable { navController.navigate("edit_profile") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = brandGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = state.fullName.ifEmpty { "Loading..." },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = brandGreen
            )

            Text(
                text = "Member since 2025",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Custom Colored Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    ProfileRowItem("Full Name", state.fullName, lightGrayLabel)
                    HorizontalDivider(color=dividerColor)

                    ProfileRowItem("Email", state.email, lightGrayLabel)
                    HorizontalDivider(color=dividerColor)

                    ProfileRowItem("Phone", state.phoneNumber, lightGrayLabel)
                    HorizontalDivider(color=dividerColor)

                    ProfileRowItem("Emergency", state.emergencyNumber, lightGrayLabel)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // RESET PASSWORD
            Text(
                text = "Reset Password",
                color = brandGreen,
                modifier = Modifier
                    .clickable { navController.navigate("reset_password") }
                    .padding(8.dp)
            )

            // LOGOUT BUTTON
            Button(
                onClick = {
                    coroutineScope.launch {
                        sessionManager.clearSession()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp)
            ) {
                Text("Logout", color = pureWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileRowItem(label: String, value: String, labelColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = labelColor)
        Text(value, fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
        )

    }
}