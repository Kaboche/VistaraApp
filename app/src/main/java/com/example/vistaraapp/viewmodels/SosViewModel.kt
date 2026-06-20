package com.example.vistaraapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaraapp.api.RetrofitClient
import com.example.vistaraapp.api_requests_responses.SosRequest
import com.example.vistaraapp.utils.TokenManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class SosViewModel : ViewModel() {

    // UI States to observe what is happening in real-time
    var isLoading by mutableStateOf(false)
        private set

    var sosMessage by mutableStateOf<String?>(null)
        private set

    //Kicks off the emergency flow: automatically pulls hardware coordinates

    @SuppressLint("MissingPermission") // Handled via UI permission guards
    fun triggerEmergencySos(context: Context, alertType: String, message: String? = null) {
        isLoading = true
        sosMessage = "Locking onto your precise GPS coordinates..."

        // 1. Initialize Google Play Services location engine
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // 2. Poll the physical hardware receivers
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // 3. Hardware success! Send coordinates to the network layer
                    sendSosToBackend(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        alertType = alertType,
                        message = message ?: "Emergency SOS Triggered from Mobile App"
                    )
                } else {
                    isLoading = false
                    sosMessage = "GPS lock failed. Please verify that device Location is switched ON."
                }
            }
            .addOnFailureListener { exception ->
                isLoading = false
                sosMessage = "Hardware failure: ${exception.localizedMessage}"
            }
    }


      //Securely transmits the payload with the Authorization Bearer token to Flask over ngrok

    private fun sendSosToBackend(latitude: Double, longitude: Double, alertType: String, message: String) {
        sosMessage = "Transmitting authenticated distress signal..."

        // Launching a Coroutine on the ViewModel's background scope
        viewModelScope.launch {
            try {
                // 1. Fetch the user's saved session token from storage
                val savedToken = TokenManager.getToken() ?: ""
                val formattedBearerToken = "Bearer $savedToken"

                // 2. Package data cleanly into our Request DTO box
                val payload = SosRequest(
                    latitude = latitude,
                    longitude = longitude,
                    alertType = alertType,
                    message = message
                )

                // 3. Execute the network POST call using the updated contract function
                val response = RetrofitClient.bookingInstance.triggerSos(
                    bearerToken = formattedBearerToken,
                    sosData = payload
                )

                // 4. Evaluate server status codes matrix
                if (response.isSuccessful && response.body()?.success == true) {
                    sosMessage = "SOS Broadcast Successful! Rangers have been dispatched to your current location."
                } else {
                    // Captures errors like 401 Unauthorized or 500 Server Error
                    sosMessage = "Alert Rejected by Server: ${response.message()}"
                }
            } catch (e: Exception) {
                // Handles timeouts, device drops connection, or ngrok tunnel down
                sosMessage = "Network Transmission Failed: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}