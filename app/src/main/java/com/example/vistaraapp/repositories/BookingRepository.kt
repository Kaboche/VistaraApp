package com.example.vistaraapp.repositories

import com.example.vistaraapp.api.ApiService
import com.example.vistaraapp.api.BookingCreationResponse
import com.example.vistaraapp.api_requests_responses.BookingsResponse
import com.example.vistaraapp.api_requests_responses.SosRequest
import com.example.vistaraapp.api_requests_responses.SosResponse
import retrofit2.Response

class BookingRepository(private val apiService: ApiService) {

    // 1. Fetch bookings
    suspend fun getUserBookings(token: String): BookingsResponse {
        return apiService.getBookings(token)
    }

    // 2. Cancel a booking
    suspend fun cancelBooking(token: String, bookingId: String): Response<BookingCreationResponse> {
        return apiService.cancelBooking(token, bookingId)
    }

    // 3. Send emergency SOS alert
    suspend fun sendSosAlert(token: String, lat: Double, lon: Double, alertType: String, message: String? = null): Response<SosResponse> {
        val requestPayload = SosRequest(
            latitude = lat,
            longitude = lon,
            alertType = alertType,
            message = message ?: "Emergency SOS Triggered from Mobile App"
        )
        return apiService.triggerSos(token, requestPayload)
    }
}