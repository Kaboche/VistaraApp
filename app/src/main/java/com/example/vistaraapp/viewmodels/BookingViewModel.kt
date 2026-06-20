package com.example.vistaraapp.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaraapp.api_requests_responses.BookingData
import com.example.vistaraapp.repositories.BookingRepository
import kotlinx.coroutines.launch

sealed class BookingUiState {
    object Loading : BookingUiState()
    data class Success(val bookings: List<BookingData>) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

class BookingViewModel(private val repository: BookingRepository) : ViewModel() {

    //  BOOKING STATE HOOKS
    private val _uiState = mutableStateOf<BookingUiState>(BookingUiState.Loading)
    val uiState: State<BookingUiState> = _uiState

    private val _cancellationStatus = mutableStateOf<String?>(null)
    val cancellationStatus: State<String?> = _cancellationStatus

    //NEW: EMERGENCY SOS STATE HOOKS
    private val _sosStatus = mutableStateOf<String?>(null)
    val sosStatus: State<String?> = _sosStatus


    // 1. OPERATION: FETCH USER BOOKINGS
    fun fetchBookings(jwtToken: String) {
        Log.d("BOOKING_DEBUG", "The raw token received by ViewModel is: '$jwtToken'")
        viewModelScope.launch {
            _uiState.value = BookingUiState.Loading
            try {
                val formattedToken = if (jwtToken.startsWith("Bearer ")) jwtToken else "Bearer $jwtToken"
                val response = repository.getUserBookings(formattedToken)

                if (response.success) {
                    _uiState.value = BookingUiState.Success(response.data)
                } else {
                    _uiState.value = BookingUiState.Error(response.message)
                }
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 404) {
                    Log.d("BOOKING_DEBUG", "Received 404, interpreting as empty bookings list")
                    _uiState.value = BookingUiState.Success(emptyList())
                } else {
                    Log.e("BOOKING_DEBUG", "Network HTTP error", e)
                    _uiState.value = BookingUiState.Error("Failed to load bookings: ${e.localizedMessage}")
                }
            } catch (e: Exception) {
                Log.e("BOOKING_DEBUG", "Network execution failed", e)
                _uiState.value = BookingUiState.Error("Failed to load bookings: ${e.localizedMessage}")
            }
        }
    }

    // 2. OPERATION: CANCEL BOOKING
    fun cancelBooking(jwtToken: String, bookingId: String) {
        Log.d("BOOKING_DEBUG", "Canceling booking ID: $bookingId")
        viewModelScope.launch {
            try {
                val formattedToken = if (jwtToken.startsWith("Bearer ")) jwtToken else "Bearer $jwtToken"
                val response = repository.cancelBooking(formattedToken, bookingId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    if (body?.success == true) {
                        _cancellationStatus.value = "SUCCESS: ${body.message}"
                        fetchBookings(jwtToken) // Refresh list automatically
                    } else {
                        _cancellationStatus.value = "FAILED: ${body?.message}"
                    }
                } else {
                    _cancellationStatus.value = "SERVER_ERROR: Code ${response.code()}"
                }
            } catch (e: Exception) {
                _cancellationStatus.value = "ERROR: ${e.localizedMessage}"
            }
        }
    }
// TRIGGER SOS ALERT WITH GPS COORDINATES
    fun triggerSosAlert(jwtToken: String, latitude: Double, longitude: Double, alertType: String, message: String? = null) {
        Log.d("SOS_DEBUG", "Triggering SOS at Lat: $latitude, Lon: $longitude with message: $message")
        viewModelScope.launch {
            _sosStatus.value = "SENDING"
            try {
                val formattedToken = if (jwtToken.startsWith("Bearer ")) jwtToken else "Bearer $jwtToken"
                val response = repository.sendSosAlert(formattedToken, latitude, longitude, alertType, message)

                if (response.isSuccessful && response.body() != null) {
                    val serverMessage = response.body()?.message ?: "Emergency services notified."
                    _sosStatus.value = "SUCCESS: $serverMessage"
                } else {
                    _sosStatus.value = "FAILED: Server error code ${response.code()}"
                }
            } catch (e: Exception) {
                _sosStatus.value = "ERROR: ${e.localizedMessage}"
            }
        }
    }

    //  STATUS CLEANUP UTILITIES
    fun clearCancellationStatus() { _cancellationStatus.value = null }

    fun clearSosStatus() { _sosStatus.value = null }
}