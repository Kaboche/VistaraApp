package com.example.vistaraapp.viewmodels

import android.util.Log // Added import for debugging!
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaraapp.api_requests_responses.BookingData
import com.example.vistaraapp.repositories.BookingRepository
import kotlinx.coroutines.launch

// Sealed class to tightly manage screen UI states
sealed class BookingUiState {
    object Loading : BookingUiState()
    data class Success(val bookings: List<BookingData>) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

class BookingViewModel(private val repository: BookingRepository) : ViewModel() {

    private val _uiState = mutableStateOf<BookingUiState>(BookingUiState.Loading)
    val uiState: State<BookingUiState> = _uiState

    fun fetchBookings(jwtToken: String) {
        // CRITICAL DEBUG LOG: Run your app and check Logcat filtered by "BOOKING_DEBUG"
        Log.d("BOOKING_DEBUG", "The raw token received by ViewModel is: '$jwtToken'")

        viewModelScope.launch {
            _uiState.value = BookingUiState.Loading
            try {
                // Ensure the Authorization header has the 'Bearer ' prefix format
                val formattedToken = if (jwtToken.startsWith("Bearer ")) jwtToken else "Bearer $jwtToken"

                Log.d("BOOKING_DEBUG", "Sending formatted header: '$formattedToken'")

                val response = repository.getUserBookings(formattedToken)

                if (response.success) {
                    _uiState.value = BookingUiState.Success(response.data)
                } else {
                    _uiState.value = BookingUiState.Error(response.message)
                }
            } catch (e: Exception) {
                // Catches network errors, such as ngrok being offline or missing internet
                Log.e("BOOKING_DEBUG", "Network execution failed", e)
                _uiState.value = BookingUiState.Error("Failed to load bookings: ${e.localizedMessage}")
            }
        }
    }
}