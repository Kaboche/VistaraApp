package com.example.vistaraapp

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTrackingScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Brand colors
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val darkText = Color(0xFF333333)

    // Permission state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLocationLoading by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            // Permission granted, get location
            Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show()
            isLocationLoading = true
            getCurrentLocation(context) { latLng ->
                currentLocation = latLng
                isLocationLoading = false
                if (latLng == null) {
                    Toast.makeText(context, "Unable to get location. Please enable GPS.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            showPermissionRationale = true
        }
    }

    // Get location if permission already granted when screen first loads
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && currentLocation == null) {
            isLocationLoading = true
            getCurrentLocation(context) { latLng ->
                currentLocation = latLng
                isLocationLoading = false
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(-1.286389, 36.817223), 15f)
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Live Tracking",
                        fontSize = 20.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = pureWhite)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                // Case 1: Permission granted and location available - Show Map
                hasLocationPermission && currentLocation != null -> {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = true
                        )
                    ) {
                        currentLocation?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "You are here",
                                snippet = "Your current location"
                            )
                        }
                    }
                }
                // Case 2: Permission granted but loading location
                hasLocationPermission && isLocationLoading -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = brandGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Getting your location...",
                            color = darkText,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Make sure GPS is enabled",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                // Case 3: Permission not granted - Show request button
                !hasLocationPermission -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = pureWhite),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Location Permission Required",
                                    fontSize = 20.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = brandGreen
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Vistara needs access to your location to track your position in the park for safety purposes.",
                                    fontSize = 14.sp,
                                    color = darkText,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                                ) {
                                    Text(
                                        text = "GRANT PERMISSION",
                                        color = pureWhite,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                }
                                if (showPermissionRationale) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Please enable location permission in Settings to use this feature.",
                                        fontSize = 12.sp,
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating STOP button
            FloatingActionButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFFD32F2F),
                contentColor = pureWhite
            ) {
                Text(
                    text = "STOP",
                    fontSize = 14.sp,
                    color = pureWhite,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

private fun getCurrentLocation(
    context: android.content.Context,
    onResult: (LatLng?) -> Unit
) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onResult(LatLng(location.latitude, location.longitude))
            } else {
                // Try to request a single location update
                onResult(null)
            }
        }.addOnFailureListener {
            onResult(null)
        }
    } catch (e: SecurityException) {
        onResult(null)
    }
}