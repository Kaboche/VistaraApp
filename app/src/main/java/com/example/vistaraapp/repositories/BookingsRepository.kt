package com.example.vistaraapp.repositories // Note the plural "repositories" to match your folder!

import com.example.vistaraapp.api.ApiService
// CRITICAL FIX: This import connects your repository to your data models!
import com.example.vistaraapp.api_requests_responses.BookingsResponse

class BookingRepository(private val apiService: ApiService) {

    // Calls the network using the security token we pass from the UI layer
    suspend fun getUserBookings(token: String): BookingsResponse {
        return apiService.getBookings(token)
    }
}